module fr.ceri.ceriplanning.hyperplaningceri {
  requires javafx.controls;
  requires javafx.fxml;


  requires org.kordamp.bootstrapfx.core;
  requires org.kordamp.ikonli.core;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.ikonli.fontawesome5;
  requires org.controlsfx.controls;



  opens fr.ceri.ceriplanning to javafx.fxml;

  exports fr.ceri.ceriplanning;
  exports fr.ceri.ceriplanning.model;

  opens fr.ceri.ceriplanning.model to javafx.fxml;
}