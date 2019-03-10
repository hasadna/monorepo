package tools.cas_proxy;

import tools.cas_proxy.AccessManager;

public class PublicAccess implements AccessManager {

  @Override
  public boolean hasReadAccess(String token, String sha256) {
    return true;
  }

  @Override
  public boolean hasWriteAccess(String token) {
    return true;
  }

  @Override
  public boolean hasDeleteAccess(String token, String sha256) {
    return true;
  }
}

