package ru.autosome.ape.di;

import ru.autosome.commons.backgroundModel.di.DiBackground;
import ru.autosome.commons.backgroundModel.di.DiBackgroundModel;
import ru.autosome.commons.backgroundModel.di.DiWordwiseBackground;
import ru.autosome.ape.calculation.findPvalue.CanFindPvalue;
import ru.autosome.ape.calculation.findPvalue.FindPvalueAPE;
import ru.autosome.ape.calculation.findPvalue.FindPvalueBsearchBuilder;
import ru.autosome.commons.backgroundModel.mono.Background;
import ru.autosome.commons.backgroundModel.mono.BackgroundModel;
import ru.autosome.commons.backgroundModel.mono.WordwiseBackground;
import ru.autosome.commons.cli.Helper;
import ru.autosome.commons.importer.DiPWMImporter;
import ru.autosome.commons.importer.PWMImporter;
import ru.autosome.commons.motifModel.di.DiPWM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FindPvalue extends ru.autosome.ape.cli.generalized.FindPvalue<DiPWM, DiBackgroundModel> {
  @Override
  protected String DOC_background_option() {
    return "ACGT - 16 numbers, comma-delimited(spaces not allowed), sum should be equal to 1, like 0.02,0.03,0.03,0.02,0.08,0.12,0.12,0.08,0.08,0.12,0.12,0.08,0.02,0.03,0.03,0.02";
  }
  @Override
  protected String DOC_run_string() {
    return "java ru.autosome.ape.di.FindPvalue";
  }

  @Override
  protected String DOC_additional_options() {
    return "These options can be used for work with PWMs on dinucleotide background models:\n" +
           "  [--from-mono]  - obtain DiPWM from mono PWM/PCM/PPM.\n";
  }

  boolean fromMononucleotide;

  @Override
  protected CanFindPvalue calculator() {
    if (cache_calculator == null) {
      if (thresholds_folder == null) {
        cache_calculator = new FindPvalueAPE<DiPWM, DiBackgroundModel>(motif, background, discretizer, max_hash_size);
      } else {
        cache_calculator = new FindPvalueBsearchBuilder(thresholds_folder).pvalueCalculator(motif);
      }
    }
    return cache_calculator;
  }

  @Override
  protected void initialize_defaults() {
    super.initialize_defaults();
    fromMononucleotide = false;
  }

  @Override
  protected void initialize_default_background() {
    background = new DiWordwiseBackground();
  }

  @Override
  protected void extract_background(String str) {
    background = DiBackground.fromString(str);
  }

  protected boolean failed_to_recognize_additional_options(String opt, List<String> argv) {
    if (opt.equals("--from-mono")) {
      fromMononucleotide = true;
      return false;
    } else {
      return true;
    }
  }

  @Override
  protected DiPWM loadMotif(String filename) {
    if (fromMononucleotide) {
      BackgroundModel backgroundMononucleotide = Background.fromDiBackground(background);
      PWMImporter importer = new PWMImporter(backgroundMononucleotide, data_model, effective_count, transpose);
      return DiPWM.fromPWM( importer.loadMotif(filename) );
    } else {
      DiPWMImporter importer = new DiPWMImporter(background, data_model, effective_count, transpose);
      return importer.loadMotif(filename);
    }
  }

  protected FindPvalue() {
    initialize_defaults();
  }

  protected static FindPvalue from_arglist(ArrayList<String> argv) {
    FindPvalue result = new FindPvalue();
    Helper.print_help_if_requested(argv, result.documentString());
    result.setup_from_arglist(argv);
    return result;
  }

  protected static FindPvalue from_arglist(String[] args) {
    ArrayList<String> argv = new ArrayList<String>();
    Collections.addAll(argv, args);
    return from_arglist(argv);
  }

  public static void main(String[] args) {
    try {
      FindPvalue cli = ru.autosome.ape.di.FindPvalue.from_arglist(args);
      System.out.println(cli.report_table().report());
    } catch (Exception err) {
      System.err.println("\n" + err.getMessage() + "\n--------------------------------------\n");
      err.printStackTrace();
      System.err.println("\n--------------------------------------\nUse --help option for help\n\n" + new FindPvalue().documentString());
      System.exit(1);
    }
  }
}
