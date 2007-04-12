package org.sakaiproject.site.tool.helper.order.rsf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.tool.helper.order.impl.SitePageEditHandler;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;


import uk.ac.cam.caret.sakai.rsf.producers.FrameAdjustingProducer;
import uk.ac.cam.caret.sakai.rsf.util.SakaiURLUtil;
import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UILabelTargetDecorator;
import uk.org.ponder.rsf.components.decorators.UITooltipDecorator;
import uk.org.ponder.rsf.flow.jsfnav.DynamicNavigationCaseReporter;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.DefaultView;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.RawViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;

/**
 * 
 * @author Joshua Ryan joshua.ryan@asu.edu
 *
 */
public class PageListProducer 
        implements ViewComponentProducer, DynamicNavigationCaseReporter, DefaultView {
    
    public static final String VIEW_ID = "PageList";
    public Map sitePages;
    public SitePageEditHandler handler;
    public MessageLocator messageLocator;
    public SessionManager sessionManager;
    public FrameAdjustingProducer frameAdjustingProducer;
    
    public String getViewID() {
        return VIEW_ID;
    }

    public void fillComponents(UIContainer tofill, ViewParameters viewparams,
            ComponentChecker checker) {

        if (handler.update) {
            
            UIBranchContainer content = UIBranchContainer.make(tofill, "content:");
            
            UIOutput.make(content, "message", messageLocator.getMessage("welcome"));
            UIOutput.make(content, "list-label", messageLocator.getMessage("curr_pages"));
            
            UIForm pageForm = UIForm.make(content, "pages-form");
            String state = "";
            
            sitePages = handler.getPages();
    
            for (Iterator it=sitePages.keySet().iterator(); it.hasNext(); ) {
                Object key = it.next();
                SitePage page = (SitePage) sitePages.get(key);
                UIBranchContainer pagerow = 
                    UIBranchContainer.make(pageForm, "page-row:", page.getId());
    
                pagerow.decorators = new DecoratorList(new UITooltipDecorator(messageLocator
                        .getMessage("page_click_n_drag")));
                
                UIOutput.make(pagerow, "page-name", page.getTitle());
                UIInput name = 
                    UIInput.make(pagerow, "page-name-input", "#{SitePageEditHandler.nil}", page.getTitle());
                UIOutput nameLabel = 
                    UIOutput.make(pagerow, "page-name-label", messageLocator.getMessage("title"));
                
                nameLabel.decorators = new DecoratorList(new UILabelTargetDecorator(name));
                
                PageEditViewParameters param = new PageEditViewParameters();
                                
                param.pageId = page.getId();

                param.viewID = PageEditProducer.VIEW_ID;
                UIInternalLink.make(pagerow, "edit-link", param).decorators =
                    new DecoratorList(new UITooltipDecorator(messageLocator
                            .getMessage("page_edit"))); 

                if (page.getTools().size() == 1) {
                    ToolConfiguration tool = (ToolConfiguration) page.getTools().get(0);

                    //if the page only has one tool and it's not site info or required we allow the user to delete it here
                    //TODO: can we get the fact that site info called us
                    if (!handler.isRequired(tool.getToolId()) && !"sakai.sitesetup".equals(tool.getToolId())
                            && !"sakai.siteinfo".equals(tool.getToolId())) {

                        param.viewID = PageDelProducer.VIEW_ID;
                        UIInternalLink.make(pagerow, "del-link", param).decorators =
                            new DecoratorList(new UITooltipDecorator(messageLocator
                                .getMessage("page_remove")));
                    }
                  
                    //allow special configuration for the iframe tool. This needs to be generalized
                    //for all tools that want special configuration and/or allow multiple instances 
                    //per site
                    if ("sakai.iframe".equals(tool.getToolId())) {
                         
                        UIInput config = UIInput.make(pagerow, "page-config-input", "#{SitePageEditHandler.nil}", 
                                tool.getPlacementConfig().getProperty("source"));
 
                        UIOutput configLabel = UIOutput.make(pagerow, "page-config-label", messageLocator
                                .getMessage("url"));
                        configLabel.decorators = new DecoratorList(new UILabelTargetDecorator(config));
                    }
                }

                if (handler.allowsHide(page)) {
                    param.viewID = PageEditProducer.VIEW_ID;
                    if (handler.isVisible(page)) {
                        param.visible = "false";
                        UIInternalLink.make(pagerow, "hide-link", param).decorators =
                            new DecoratorList(new UITooltipDecorator(messageLocator
                                .getMessage("page_hide")));
                        param.visible = "true";
                        UIInternalLink.make(pagerow, "show-link-off", param).decorators =
                            new DecoratorList(new UITooltipDecorator(messageLocator
                                .getMessage("page_show")));
                    }
                    else {
                        param.visible = "true";
                        UIInternalLink.make(pagerow, "show-link", param).decorators =
                            new DecoratorList(new UITooltipDecorator(messageLocator
                                .getMessage("page_show")));
                     
                        param.visible = "false";
                        UIInternalLink.make(pagerow, "hide-link-off", param).decorators =
                            new DecoratorList(new UITooltipDecorator(messageLocator
                                .getMessage("page_hide")));
                    }
                }

                state += page.getId() + " ";
            }

            PageAddViewParameters addParam = new PageAddViewParameters();
            addParam.mode = "list";
            addParam.viewID = PageAddProducer.VIEW_ID;
            UIInternalLink.make(content, "add-link", addParam).decorators =
                new DecoratorList(new UITooltipDecorator(messageLocator
                        .getMessage("page_show_add")));
            
            UIOutput.make(content, "add-page", messageLocator.getMessage("show_add"));
            
            UIOutput.make(pageForm, "del-message", messageLocator.getMessage("del_message"));
            UIOutput.make(pageForm, "exit-message", messageLocator.getMessage("exit_message"));
            UIOutput.make(pageForm, "reset-message", messageLocator.getMessage("confirm_reset_message"));
        
            UIInput.make(pageForm, "state-init", "#{SitePageEditHandler.state}", state);
            UICommand.make(pageForm, "save", "#{SitePageEditHandler.savePages}").decorators = 
                new DecoratorList(new UITooltipDecorator(messageLocator
                        .getMessage("save_message")));

            UICommand.make(pageForm, "revert", "#{SitePageEditHandler.cancel}").decorators = 
                new DecoratorList(new UITooltipDecorator(messageLocator
                        .getMessage("cancel_message")));

            if (handler.isSiteOrdered()) {
                UICommand.make(pageForm, "reset", "#{SitePageEditHandler.reset}").decorators = 
                    new DecoratorList(new UITooltipDecorator(messageLocator
                            .getMessage("reset_message")));
            }
 
            frameAdjustingProducer.fillComponents(tofill, "resize", "resetFrame");
 
        }
        else {
            //error messages
            UIBranchContainer error = UIBranchContainer.make(tofill, "error:");
            UIOutput.make(error, "message", messageLocator
                    .getMessage("access_error"));
        }
    }

    public List reportNavigationCases() {
        Tool tool = handler.getCurrentTool();
        List togo = new ArrayList();
        togo.add(new NavigationCase("done", new RawViewParameters(SakaiURLUtil.getHelperDoneURL(tool, sessionManager))));

        return togo;
    }
}
