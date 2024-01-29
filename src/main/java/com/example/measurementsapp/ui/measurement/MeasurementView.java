package com.example.measurementsapp.ui.measurement;

import com.example.measurementsapp.fileexport.CsvFileExporter;
import com.example.measurementsapp.fileexport.FileExporter;
import com.example.measurementsapp.fileexport.PdfFileExporter;
import com.example.measurementsapp.fileexport.XlsFileExporter;
import com.example.measurementsapp.fileimport.MeasurementFileReader;
import com.example.measurementsapp.measurement.MeasurementService;
import com.example.measurementsapp.measurement.model.Measurement;
import com.example.measurementsapp.ui.MainView;
import com.example.measurementsapp.ui.charts.ChartsDialog;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@PermitAll
@PageTitle("Pomiary")
@Route(value = StringUtils.EMPTY, layout = MainView.class)
@RouteAlias(value = "measurements", layout = MainView.class)
public class MeasurementView extends VerticalLayout {

  private final Grid<Measurement> grid = new Grid<>(Measurement.class);
  private final MeasurementService measurementService;
  private final Map<String, MeasurementFileReader> measurementFileReaderMap;
  private final Map<String, FileExporter> fileExporterMap;
  private final Button editButton;
  private final Button deleteButton;

  public MeasurementView(MeasurementService measurementService,
      Map<String, MeasurementFileReader> measurementFileReaderMap,
      Map<String, FileExporter> fileExporterMap) {
    this.measurementService = measurementService;
    this.measurementFileReaderMap = measurementFileReaderMap;
    this.fileExporterMap = fileExporterMap;
    this.editButton = createEditButton();
    this.deleteButton = createDeleteButton();
    setSizeFull();
    grid.addSelectionListener(e -> {
      editButton.setEnabled(!e.getAllSelectedItems().isEmpty());
      deleteButton.setEnabled(!e.getAllSelectedItems().isEmpty());
    });
    createToolbar();
    createGrid();
  }

  private void createToolbar() {
    var newButton = new Button("Nowy");
    newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    newButton.addClickListener(
        e -> UI.getCurrent().navigate(MeasurementForm.class, "new"));

    var clearButton = new Button("Usuń wszystko");
    clearButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
    clearButton.addClickListener(
        e -> {
          measurementService.deleteAll();
          updateGrid();
        });

    var importDataButton = new Button("Importuj");
    importDataButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    importDataButton.addClickListener(e -> {
      var importDialog = new MeasurementImportDialog(measurementFileReaderMap);
      importDialog.addOpenedChangeListener(event -> {
        if (!event.isOpened()) {
          updateGrid();
        }
      });
      add(importDialog);
      importDialog.open();
    });

    var displayChartsButton = new Button("Pokaż wykresy");
    displayChartsButton.addClickListener(e -> {
      var chartsDialog = new ChartsDialog(measurementService.findAll());
      chartsDialog.addOpenedChangeListener(event -> {
        if (!event.isOpened()) {
          updateGrid();
        }
      });
      add(chartsDialog);
      chartsDialog.open();
    });

    var exportFileButton = new Button("Eksportuj");
    var menuBar = new MenuBar();
    menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
    var menuItem = menuBar.addItem(exportFileButton);
    var subMenu = menuItem.getSubMenu();
    subMenu.addItem("CSV", e -> downloadFile(fileExporterMap.get(CsvFileExporter.BEAN_NAME)));
    subMenu.addItem("PDF", e -> downloadFile(fileExporterMap.get(PdfFileExporter.BEAN_NAME)));
    subMenu.addItem("Excel", e -> downloadFile(fileExporterMap.get(XlsFileExporter.BEAN_NAME)));

    var toolbar = new HorizontalLayout();
    toolbar.add(importDataButton, newButton, editButton, deleteButton, clearButton,
        displayChartsButton, menuBar);
    add(toolbar);
  }

  private void downloadFile(FileExporter fileExporter) {
    try{
      var item = fileExporter.export(measurementService.findAll());
      var input = new ByteArrayInputStream(item.content());
      var anchor = downloadAnchor(
          item.fileName(), input, this.getUI().orElseThrow());
      this.add(anchor);
    } catch (IOException e) {
      log.error("Export to file error ", e);
    }
  }

  public static Anchor downloadAnchor(String fileName, InputStream inputStream, UI current) {
    Anchor download = new Anchor(new StreamResource(fileName, () -> inputStream), "");
    download.getElement().setAttribute("download", true);
    current.getPage().open(download.getHref());
    download.setVisible(false);
    return download;
  }

  private Button createEditButton() {
    var editButton = new Button("Edytuj");
    editButton.setEnabled(false);
    editButton.addClickListener(
        e -> grid.asSingleSelect().getOptionalValue().ifPresent(measurement -> {
          UI.getCurrent().navigate(MeasurementForm.class, measurement.getId().toString());
        }));
    return editButton;
  }

  private Button createDeleteButton() {
    var deleteButton = new Button("Usuń");
    deleteButton.setEnabled(false);
    deleteButton.addClickListener(
        e -> grid.asSingleSelect().getOptionalValue().ifPresent(measurement -> {
          measurementService.delete(measurement.getId());
          updateGrid();
        }));
    return deleteButton;
  }

  private void createGrid() {
    grid.setColumns();
    updateGrid();

    grid.addColumn(Measurement::getTimeInSeconds)
        .setHeader("Czas [s]");

    grid.addColumn(Measurement::getMeasureValueOneAsString)
        .setHeader("Wartość pomiaru");

    grid.addColumn(Measurement::getMeasureValueTwoAsString)
        .setHeader("Wartość pomiaru");

    grid.setSortableColumns();
    add(grid);
  }

  public void updateGrid() {
    DataProvider<Measurement, Void> dataProvider =
        DataProvider.fromCallbacks(
            query -> {
              int offset = query.getOffset();
              int limit = query.getLimit();
              return measurementService.findAll().stream()
                  .skip(offset)
                  .limit(limit);
            },
            query -> measurementService.findAll().size());
    grid.setDataProvider(dataProvider);
  }
}
