package projects.noloan.app.filter;

public interface SpamFilterAlgorithm {
  boolean lineIsSpam(String line);
}
