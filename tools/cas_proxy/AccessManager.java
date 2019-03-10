package tools.cas_proxy;

public interface AccessManager {
  boolean hasReadAccess(String token, String sha256);

  boolean hasWriteAccess(String token);

  boolean hasDeleteAccess(String token, String sha256);
}

