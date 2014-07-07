package ru.autosome.commons.importer;

import ru.autosome.commons.motifModel.Named;
import ru.autosome.commons.motifModel.ScoringModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public abstract class MotifImporter<ModelType extends Named & ScoringModel> {
  abstract public ModelType transformToPWM(double matrix[][], String name);

  public List<ModelType> loadPWMsFromFile(File pathToPWMs) throws FileNotFoundException {
    List<ModelType> pwms = new ArrayList<ModelType>();
    BufferedPushbackReader reader = new BufferedPushbackReader(new FileInputStream(pathToPWMs));
    boolean canExtract = true;
    while (canExtract) {
      PMParser parser = PMParser.loadFromStream(reader);
      canExtract = canExtract && (parser != null);
      if (parser == null) {
        canExtract = false;
      } else {
        ModelType pwm = transformToPWM(parser.matrix(), parser.name());
        pwms.add(pwm);
      }
    }
    return pwms;
  }

  public ModelType loadPWMFromParser(PMParser parser) {
    return transformToPWM(parser.matrix(), parser.name());
  }

  public List<ModelType> loadPWMsFromFolder(File pathToPWMs) {
    List<ModelType> result = new ArrayList<ModelType>();
    File[] files = pathToPWMs.listFiles();
    if (files == null) {
      return result;
    }
    for (File file : files) {
      result.add(loadPWMFromFile(file));
    }
    return result;
  }

  public ModelType loadPWMFromFile(File file) {
    PMParser parser = PMParser.from_file(file);
    ModelType pwm = transformToPWM(parser.matrix(), parser.name());
    if (pwm.getName() == null || pwm.getName().isEmpty()) {
      pwm.setName(file.getName().replaceAll("\\.[^.]+$", ""));
    }
    return pwm;
  }
}
