package projects.noloan.app.filter.bayes;

public class WordInfo {
  private int spamCount; // number of this words appearances in spam messages
  private int hamCount; // number of this words appearances in ham messages
  private float spamRate; // spamCount divided by total spam count
  private float hamRate; // hamCount divided by total ham count
  private float probOfSpam; // probability of word being spam

  WordInfo() {
    spamCount = 0;
    hamCount = 0;
    spamRate = 0.0f;
    hamRate = 0.0f;
    probOfSpam = 0.4f;
  }

  void countSpam() {
    spamCount++;
  }

  void countHam() {
    hamCount++;
  }

  // calculates the probability of spam,
  // and gives the smallest and biggest probabilities more precedence
  void calculateProbability(int totSpam, int totHam) {
    spamRate = spamCount / (float) totSpam;
    hamRate = hamCount / (float) totHam;

    if (spamRate + hamRate > 0) {
      probOfSpam = spamRate / (spamRate + hamRate);
    }
    if (probOfSpam < 0.01f) {
      probOfSpam = 0.01f;
    } else if (probOfSpam > 0.99f) {
      probOfSpam = 0.99f;
    }
  }

  float getProbOfSpam() {
    return probOfSpam;
  }
}
