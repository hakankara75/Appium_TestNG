package kitapyurdu.tests;

import kitapyurdu.pages.KitapYurdu_Pozitif_Page;
import org.testng.annotations.Test;

public class KitapYurdu_Pozitif_Test {
    KitapYurdu_Pozitif_Page page=new KitapYurdu_Pozitif_Page();

    @Test
    public void click() {
        page.ilkKitapSec();

    }

    @Test
    public void scrollClick() {
        page.scrollClick();
    }
}
