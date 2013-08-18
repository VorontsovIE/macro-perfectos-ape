package ru.autosome.jMacroape;

public class SequenceWithSNP {
  //public String seq_w_snp;
  private String left;
  private String right;
  private char[] mid;

  // line should finish with sequence (which doesn't have spaces).
  // Example:
  // input:  "GATTCAAAGGTTCTGAATTCCACAAC[a/g]GCTTTCCTGTGTTTTTGCAGCCAGA"
  // possible SNP formats: [a/g]; [ag]; a/g; a/g/c; [agc]; [a/g/c] and so on
  public SequenceWithSNP(String seq_w_snp) {
    String[] seq_parts = seq_w_snp.split("\\[|\\]");  // split by [ or ]

    if (seq_parts.length == 1) { //   accT/Acca  or  accT/A/Gcca
      int left_separator = seq_w_snp.indexOf("/");
      int right_separator = seq_w_snp.lastIndexOf("/");
      left = seq_w_snp.substring(0,left_separator - 1);
      right = seq_w_snp.substring(right_separator + 2, seq_w_snp.length());
      mid = seq_w_snp.substring(left_separator - 1, right_separator + 2).replaceAll("/","").toCharArray();
    } else if (seq_parts.length == 3) { // acc[T/A]cca  or acc[TA]cca or  acc[T/A/G]cca  or acc[TAG]cca
      left = seq_parts[0];
      mid = seq_parts[1].replaceAll("/","").toCharArray();
      right = seq_parts[2];
    } else {
      throw new IllegalArgumentException("Can't parse sequence with SNPs: "+ seq_w_snp);
    }
  }

  public int pos_of_snp() {
    return left.length();
  }
  public int num_cases(){
    return mid.length;
  }

  // output: ["GATTCAAAGGTTCTGAATTCCACAACaGCTTTCCTGTGTTTTTGCAGCCAGA",
  //          "GATTCAAAGGTTCTGAATTCCACAACgGCTTTCCTGTGTTTTTGCAGCCAGA"]
  public Sequence[] sequence_variants() {
    Sequence[] result = new Sequence[num_cases()];
    for (int i = 0; i < num_cases(); ++i) {
      result[i] = new Sequence(left + mid[i] + right);
    }
    return result;
  }
  public int length() {
    return left.length() + 1 + right.length();
  }
  public int left_shift(int motif_length) {
    return Math.max(0, pos_of_snp() - motif_length + 1);
  }

  public static Sequence trim_to_motif_length(Sequence seq, int snp_position, int motif_length) {
    return seq.substring(Math.max(0, snp_position - motif_length + 1),
            Math.min(seq.length(), snp_position + motif_length)); // end point not included
  }

  // trim sequence variants to size that pwm will overlap place of polymorphism at each position
  public Sequence[] trimmed_sequence_variants(PM pm){
    int pos_of_snp = pos_of_snp();
    Sequence[] sequence_variants = sequence_variants();
    Sequence[] trimmed_sequence_variants = new Sequence[num_cases()];
    for(int i = 0; i < num_cases(); ++i) {
      trimmed_sequence_variants[i] = trim_to_motif_length(sequence_variants[i], pos_of_snp, pm.length());
    }
    return trimmed_sequence_variants;
  }

  @Override
  public String toString() {
    String mid_variants = "" + mid[0];
    for (int i = 1; i < num_cases(); ++i) {
      mid_variants += "/" + mid[i];
    }
    return left + "[" + mid_variants + "]" + right;
  }

}
