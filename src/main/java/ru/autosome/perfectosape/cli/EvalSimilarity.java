package ru.autosome.perfectosape.cli;

import ru.autosome.perfectosape.*;
import ru.autosome.perfectosape.calculations.CanFindPvalue;
import ru.autosome.perfectosape.calculations.ComparePWM;
import ru.autosome.perfectosape.calculations.CountingPWM;
import ru.autosome.perfectosape.calculations.FindThresholdAPE;

import java.util.ArrayList;
import java.util.Collections;

public class EvalSimilarity {
  private static final String DOC =
   "Command-line format:\n" +
    "java ru.autosome.perfectosape.cli.EvalSimilarity <1st matrix pat-file> <2nd matrix pat-file> [options]\n" +
    "\n" +
    "Options:\n" +
    "  [-p <P-value>]\n" +
    "  [-d <discretization level>]\n" +
    "  [--pcm] - treat the input file as Position Count Matrix. PCM-to-PWM transformation to be done internally.\n" +
    "  [--ppm] or [--pfm] - treat the input file as Position Frequency Matrix. PPM-to-PWM transformation to be done internally.\n" +
    "  [--effective-count <count>] - effective samples set size for PPM-to-PWM conversion (default: 100). \n" +
    "  [--boundary lower|upper] Upper boundary (default) means that the obtained P-value is greater than or equal to the requested P-value\n" +
    "  [-b <background probabilities] ACGT - 4 numbers, comma-delimited(spaces not allowed), sum should be equal to 1, like 0.25,0.24,0.26,0.25\n" +
    "  [--first-threshold <threshold for the first matrix>]\n" +
    "  [--second-threshold <threshold for the second matrix>]\n" +
    "\n" +
    "examples:\n" +
    "  java ru.autosome.perfectosape.cli.EvalSimilarity motifs/KLF4_f2.pat motifs/SP1_f1.pat -p 0.0005 -d 100 -b 0.3,0.2,0.2,0.3\n";

  private BackgroundModel firstBackground, secondBackground;
  private Double discretization;
  private double pvalue;
  private BoundaryType pvalue_boundary;
  private String firstPMFilename, secondPMFilename;
  private DataModel dataModelFirst, dataModelSecond;

  private Integer max_hash_size;
  private Integer max_pair_hash_size;

  private Double effectiveCountFirst, effectiveCountSecond;

  private Double predefinedFirstThreshold, predefinedSecondThreshold;

  private PWM firstPWM, secondPWM;


  private void extract_first_pm_filename(ArrayList<String> argv) {
    if (argv.isEmpty()) {
      throw new IllegalArgumentException("No input. You should specify input file");
    }
    firstPMFilename = argv.remove(0);
  }

  private void extract_second_pm_filename(ArrayList<String> argv) {
    if (argv.isEmpty()) {
      throw new IllegalArgumentException("No input. You should specify input file");
    }
    secondPMFilename = argv.remove(0);
  }

  private void initialize_defaults() {
    firstBackground = new WordwiseBackground();
    secondBackground = new WordwiseBackground();
    dataModelFirst = DataModel.PWM;
    dataModelSecond = DataModel.PWM;
    effectiveCountFirst = 100.0;
    effectiveCountSecond = 100.0;
    pvalue = 0.0005;
    discretization = 10.0;

    max_hash_size = 10000000;
    max_pair_hash_size = 10000;
    pvalue_boundary = BoundaryType.UPPER;
  }

  private void extract_option(ArrayList<String> argv) {
    String opt = argv.remove(0);
    if (opt.equals("-b")) {
      BackgroundModel background = Background.fromString(argv.remove(0));
      firstBackground = background;
      secondBackground = background;
    } else if (opt.equals("-b1")) {
      firstBackground = Background.fromString(argv.remove(0));
    } else if (opt.equals("-b2")) {
      secondBackground = Background.fromString(argv.remove(0));
    } else if (opt.equals("--max-hash-size")) {
      max_hash_size = Integer.valueOf(argv.remove(0));
    } else if (opt.equals("--max-2d-hash-size")) {
      max_pair_hash_size = Integer.valueOf(argv.remove(0));
    } else if (opt.equals("-d")) {
      discretization = Double.valueOf(argv.remove(0));
    } else if (opt.equals("--pcm")) {
      dataModelFirst = DataModel.PCM;
      dataModelSecond = DataModel.PCM;
    } else if (opt.equals("--ppm") || opt.equals("--pfm")) {
      dataModelFirst = DataModel.PPM;
      dataModelSecond = DataModel.PPM;
    } else if (opt.equals("--effective-count")) {
      Double effectiveCount = Double.valueOf(argv.remove(0));
      effectiveCountFirst = effectiveCount;
      effectiveCountSecond = effectiveCount;
    } else if (opt.equals("--first-threshold")) {
      predefinedFirstThreshold = Double.valueOf(argv.remove(0));
    } else if (opt.equals("--second-threshold")) {
      predefinedSecondThreshold = Double.valueOf(argv.remove(0));
    } else {
      throw new IllegalArgumentException("Unknown option '" + opt + "'");
    }
  }

  void setup_from_arglist(ArrayList<String> argv) {
    extract_first_pm_filename(argv);
    extract_second_pm_filename(argv);
    while (argv.size() > 0) {
      extract_option(argv);
    }
    firstPWM = Helper.load_pwm(PMParser.from_file_or_stdin(firstPMFilename), dataModelFirst, firstBackground, effectiveCountFirst);
    secondPWM = Helper.load_pwm(PMParser.from_file_or_stdin(secondPMFilename), dataModelSecond, secondBackground, effectiveCountSecond);
  }

  private EvalSimilarity() {
    initialize_defaults();
  }

  private static EvalSimilarity from_arglist(ArrayList<String> argv) {
    EvalSimilarity result = new EvalSimilarity();
    ru.autosome.perfectosape.cli.Helper.print_help_if_requested(argv, DOC);
    result.setup_from_arglist(argv);
    return result;
  }

  private static EvalSimilarity from_arglist(String[] args) {
    ArrayList<String> argv = new ArrayList<String>();
    Collections.addAll(argv, args);
    return from_arglist(argv);
  }

  ComparePWM calculator() {
    ComparePWM result = new ComparePWM(new CountingPWM(firstPWM.discrete(discretization), firstBackground, max_hash_size),
                                       new CountingPWM(secondPWM.discrete(discretization), secondBackground, max_hash_size));
    result.max_pair_hash_size = max_pair_hash_size;
    return result;
  }

  OutputInformation report_table_layout() {
    OutputInformation infos = new OutputInformation();

    infos.add_parameter("V", "discretization", discretization);
    if (predefinedFirstThreshold == null || predefinedSecondThreshold == null) {
      infos.add_parameter("P", "requested P-value", pvalue);
    }
    if (predefinedFirstThreshold != null) {
      infos.add_parameter("T1", "threshold for the 1st matrix", predefinedFirstThreshold);
    }
    if (predefinedSecondThreshold != null) {
      infos.add_parameter("T2", "threshold for the 2nd matrix", predefinedSecondThreshold);
    }
    infos.add_parameter("PB", "P-value boundary", pvalue_boundary);
    if (firstBackground.equals(secondBackground)) {
      infos.background_parameter("B", "background", firstBackground);
    } else {
      infos.background_parameter("B1", "background for the 1st model", firstBackground);
      infos.background_parameter("B2", "background for the 2nd model", secondBackground);
    }

    return infos;
  }

  OutputInformation report_table(ComparePWM.SimilarityInfo info) {
    OutputInformation infos = report_table_layout();
    infos.add_resulting_value("S", "similarity", info.similarity());
    infos.add_resulting_value("D", "distance (1-similarity)", info.distance());
    infos.add_resulting_value("L", "length of the alignment", info.alignment.length());
    infos.add_resulting_value("SH", "shift of the 2nd PWM relative to the 1st", info.alignment.shift());
    infos.add_resulting_value("OR", "orientation of the 2nd PWM relative to the 1st", info.alignment.orientation());
    infos.add_resulting_value("A1", "aligned 1st matrix", info.alignment.first_pwm_alignment());
    infos.add_resulting_value("A2", "aligned 2nd matrix", info.alignment.second_pwm_alignment());
    infos.add_resulting_value("W", "number of words recognized by both models (model = PWM + threshold)", info.recognizedByBoth );
    infos.add_resulting_value("W1", "number of words and recognized by the first model", info.recognizedByFirst );
    infos.add_resulting_value("P1", "P-value for the 1st matrix", info.realPvalueFirst() );
    if (predefinedFirstThreshold == null) {
      infos.add_resulting_value("T1", "threshold for the 1st matrix", thresholdFirst() );
    }
    infos.add_resulting_value("W2", "number of words recognized by the 2nd model", info.recognizedBySecond );
    infos.add_resulting_value("P2", "P-value for the 2nd matrix", info.realPvalueSecond() );
    if (predefinedSecondThreshold == null) {
      infos.add_resulting_value("T2", "threshold for the 2nd matrix", thresholdSecond() );
    }
    return infos;
  }

  double thresholdFirst() {
    if (predefinedFirstThreshold != null) {
      return predefinedFirstThreshold;
    } else {
      FindThresholdAPE pvalue_calculator = new FindThresholdAPE(firstPWM,
                                                                firstBackground,
                                                                discretization,
                                                                pvalue_boundary,
                                                                max_hash_size);
      return pvalue_calculator.find_threshold_by_pvalue(pvalue).threshold;
    }
  }

  double thresholdSecond() {
    if (predefinedSecondThreshold != null) {
      return predefinedSecondThreshold;
    } else {
        FindThresholdAPE pvalue_calculator = new FindThresholdAPE(secondPWM,
                                                                  secondBackground,
                                                                  discretization,
                                                                  pvalue_boundary,
                                                                  max_hash_size);
      return pvalue_calculator.find_threshold_by_pvalue(pvalue).threshold;
    }
  }

  OutputInformation report_table() throws Exception {
    ComparePWM.SimilarityInfo results = calculator().jaccard(thresholdFirst() * discretization, thresholdSecond() * discretization);
    return report_table(results);
  }

  public static void main(String[] args) {
    try {
      EvalSimilarity cli = EvalSimilarity.from_arglist(args);
      System.out.println(cli.report_table().report());
    } catch (Exception err) {
      System.err.println("\n" + err.getMessage() + "\n--------------------------------------\n");
      err.printStackTrace();
      System.err.println("\n--------------------------------------\nUse --help option for help\n\n" + DOC);
      System.exit(1);
    }
  }
}