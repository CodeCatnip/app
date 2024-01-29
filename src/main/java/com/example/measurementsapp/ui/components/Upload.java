package com.example.measurementsapp.ui.components;

import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.UploadI18N.Uploading;
import com.vaadin.flow.component.upload.UploadI18N.Uploading.Status;
import org.apache.commons.lang3.StringUtils;

public class Upload extends com.vaadin.flow.component.upload.Upload {

  public Upload(Receiver receiver) {
    super(receiver);
    UploadI18N i18n = new UploadI18N();
    UploadI18N.AddFiles addFiles = new UploadI18N.AddFiles();
    addFiles.setMany("Wgraj pliki..");
    addFiles.setOne("Wgraj plik..");
    i18n.setAddFiles(addFiles);
    UploadI18N.DropFiles dropFiles = new UploadI18N.DropFiles();
    dropFiles.setMany("Upuść pliki..");
    dropFiles.setOne("Upuść plik..");
    i18n.setDropFiles(dropFiles);
    UploadI18N.Error error = new UploadI18N.Error();
    error.setFileIsTooBig("Plik jest za duży");
    error.setIncorrectFileType("Niepoprawny typ pliku");
    error.setTooManyFiles("Zbyt wiele plików");
    i18n.setError(error);
    setI18n(i18n);
    getStyle().set("border", "none");
    UploadI18N.Uploading uploading = new Uploading();

    var status = new Status();
    status.setConnecting(StringUtils.EMPTY);
    status.setHeld(StringUtils.EMPTY);
    status.setProcessing("Przetwarzanie pliku...");
    status.setStalled(StringUtils.EMPTY);
    uploading.setStatus(status);
    i18n.setUploading(uploading);
  }
}
