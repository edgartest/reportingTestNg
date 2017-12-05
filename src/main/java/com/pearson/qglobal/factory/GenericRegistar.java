package com.pearson.qglobal.factory;

import com.pearson.qglobal.common.factory.QGPageObjectRegistrar;
import com.pearson.qglobal.common.pageobject.QGPageObject;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.openqa.selenium.WebDriver;

public class GenericRegistrar extends QGPageObjectRegistrar {
    public GenericRegistrar() {
    }

    public void setupAllPageObjects(WebDriver driver) throws Exception {
        this.setup(Arrays.asList(QGPageObject.class));
        this.registerAllClasses(driver);
    }

    public void setupDefaults(WebDriver driver) throws Exception {
        this.setupAllPageObjects(driver);
    }

    public void setup(List<?> list) {
        Iterator var2 = list.iterator();

        while(var2.hasNext()) {
            Object c = var2.next();
            if (c instanceof Class) {
                try {
                    this.addSubTypesOf((Class)c);
                } catch (Exception var5) {
                    var5.printStackTrace();
                }
            }
        }

    }
}
