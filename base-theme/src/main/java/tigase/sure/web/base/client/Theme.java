/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client;

import com.google.gwt.resources.client.CssResource;
import tigase.sure.web.base.client.widgets.VerticalTabLayoutPanel.VerticalTabLayoutStyle;

/**
 *
 * @author andrzej
 */
public interface Theme extends Icons {
        
        @Source("style.css")
        Style style();

        @Source("verticaltabpanel.css")
        VerticalTabLayoutStyle verticalTabPanelStyles();
        
}