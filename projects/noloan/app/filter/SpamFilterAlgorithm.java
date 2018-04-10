package projects.noloan.app.filter;

public interface SpamFilterAlgorithm {
  boolean isSpam(String message);
}
