module fr.ceri.ceriplanning.hyperplaningceri {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.kordamp.bootstrapfx.core;

  opens fr.ceri.ceriplanning.hyperplaningceri to javafx.fxml;
  exports fr.ceri.ceriplanning.hyperplaningceri;
}