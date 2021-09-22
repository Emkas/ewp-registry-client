package eu.erasmuswithoutpaper.registryclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import eu.erasmuswithoutpaper.registryclient.RegistryClient.RefreshFailureException;

import org.junit.Test;

public class GenericTests extends TestBase {

  @Test
  public void testReadingResources() {
    // using isEqualToIgnoringWhitespace to prevent EOL problems
    assertThat(getFileAsString("file.txt")).isEqualToIgnoringWhitespace("Content.");
  }

  @Test
  public void testVersionComparison() {
    assertThat(CatalogueDocument.doesVersionXMatchMinimumRequiredVersionY("1.6.0", "1.10.0"))
        .isFalse();
    assertThat(CatalogueDocument.doesVersionXMatchMinimumRequiredVersionY("1.10.0", "1.6.0"))
        .isTrue();
    assertThat(CatalogueDocument.doesVersionXMatchMinimumRequiredVersionY("1.6.0", "1.6.0"))
        .isTrue();
    assertThat(CatalogueDocument.doesVersionXMatchMinimumRequiredVersionY("1.10", "1.6.0"))
        .isFalse();
    assertThat(CatalogueDocument.doesVersionXMatchMinimumRequiredVersionY("1.10.0", "1.6.0x"))
        .isFalse();
  }

  @Test
  public void testXXE() {
    FakeCatalogueFetcher fetcher = new FakeCatalogueFetcher();
    ClientImplOptions options = new ClientImplOptions();
    options.setCatalogueFetcher(fetcher);
    fetcher.setCatalogueToUse("xxe.xml");
    try (RegistryClient cli = new ClientImpl(options)) {
      cli.refresh();
      fail("Exception expected.");
    } catch (RefreshFailureException e) {
      assertThat(e.getCause().getCause().getMessage()).contains("DOCTYPE is disallowed");
    }
  }
}
