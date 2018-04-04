package projects.noloan.app.filter.bayes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import projects.noloan.app.filter.SpamFilterAlgorithm;

public class BayesFilter implements SpamFilterAlgorithm {
  private HashMap<String, WordInfo> words = new HashMap<>();
  private int totalSpamCount = 0;
  private int totalHamCount = 0;

  public boolean lineIsSpam(String line) {
    return calculateBayes(makeWordList(line));
  }

  public void train(List<String> spam, List<String> ham) {
    for (String spamWord : spam) {
      trainIteration(spamWord, WordType.SPAM);
    }
    for (String hamWord : ham) {
      trainIteration(hamWord, WordType.HAM);
    }

    for (String key : words.keySet()) {
      words.get(key).calculateProbability(totalSpamCount, totalHamCount);
    }
  }

  private void trainIteration(String string, WordType type) {
    for (String word : string.split(" ")) {
      word = word.replaceAll("\\W", "").toLowerCase();

      if (word.length() < 1 || word.matches("^[0-9]+$")) {
        return;
      }

      WordInfo w;
      if (words.containsKey(word)) {
        w = words.get(word);
      } else {
        w = new WordInfo();
        words.put(word, w);
      }
      if (type == WordType.HAM) {
        w.countHam();
        totalHamCount++;
      } else if (type == WordType.SPAM) {
        w.countSpam();
        totalSpamCount++;
      }
    }
  }

  private ArrayList<WordInfo> makeWordList(String sms) {
    ArrayList<WordInfo> wordInfoList = new ArrayList<>();
    for (String word : sms.split(" ")) {
      word = word.replaceAll("\\W", "");
      word = word.toLowerCase();
      WordInfo w;
      if (words.containsKey(word)) {
        w = words.get(word);
      } else {
        w = new WordInfo();
      }
      wordInfoList.add(w);
    }
    return wordInfoList;
  }

  private boolean calculateBayes(ArrayList<WordInfo> sms) {
    float probabilityOfPositiveProduct = 1.0f;
    float probabilityOfNegativeProduct = 1.0f;
    for (int i = 0; i < sms.size(); i++) {
      WordInfo wordInfo = sms.get(i);
      probabilityOfPositiveProduct *= wordInfo.getProbOfSpam();
      probabilityOfNegativeProduct *= (1.0f - wordInfo.getProbOfSpam());
    }
    float probOfSpam =
        probabilityOfPositiveProduct
            / (probabilityOfPositiveProduct + probabilityOfNegativeProduct);
    return probOfSpam > 0.9f;
  }
}
