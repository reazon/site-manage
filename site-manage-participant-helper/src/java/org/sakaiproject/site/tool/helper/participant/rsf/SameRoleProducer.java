package org.sakaiproject.site.tool.helper.participant.rsf;

import java.util.Collection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.site.tool.helper.participant.rsf.AddViewParameters;
import org.sakaiproject.site.tool.helper.participant.impl.SiteAddParticipantHandler;
import org.sakaiproject.site.util.Participant;
import org.sakaiproject.site.util.SiteComparator;
import org.sakaiproject.site.util.SiteConstants;
import org.sakaiproject.util.SortedIterator;
import org.sakaiproject.util.SortedIterator;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;

import uk.ac.cam.caret.sakai.rsf.producers.FrameAdjustingProducer;
import uk.ac.cam.caret.sakai.rsf.util.SakaiURLUtil;
import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.messageutil.TargettedMessageList;
import uk.org.ponder.rsf.components.UIBoundBoolean;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.components.UISelectChoice;
import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UILabelTargetDecorator;
import uk.org.ponder.rsf.components.decorators.UITooltipDecorator;
import uk.org.ponder.rsf.evolvers.TextInputEvolver;
import uk.org.ponder.rsf.evolvers.FormatAwareDateInputEvolver;
import uk.org.ponder.rsf.flow.ActionResultInterceptor;
import uk.org.ponder.rsf.flow.ARIResult;
import uk.org.ponder.rsf.flow.ActionResultInterceptor;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.DefaultView;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.RawViewParameters;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;
import uk.org.ponder.stringutil.StringList;

/**
 * Assign same role while adding participant
 * @author
 *
 */
public class SameRoleProducer implements ViewComponentProducer, NavigationCaseReporter, ActionResultInterceptor {

	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(SameRoleProducer.class);
	
    public SiteAddParticipantHandler handler;
    public static final String VIEW_ID = "SameRole";
    public MessageLocator messageLocator;
    public FrameAdjustingProducer frameAdjustingProducer;
    public SessionManager sessionManager;
    public SiteService siteService = null;
    public void setSiteService(SiteService siteService)
	{
		this.siteService = siteService;
	}

    public String getViewID() {
        return VIEW_ID;
    }
    
    private TargettedMessageList tml;
	public void setTargettedMessageList(TargettedMessageList tml) {
		this.tml = tml;
	}
	
	public UserDirectoryService userDirectoryService;
	public void setUserDiretoryService(UserDirectoryService userDirectoryService)
	{
		this.userDirectoryService = userDirectoryService;
	}

    public void fillComponents(UIContainer tofill, ViewParameters arg1, ComponentChecker arg2) {
    	
    	UIBranchContainer content = UIBranchContainer.make(tofill, "content:");
        
    	UIForm sameRoleForm = UIForm.make(content, "sameRole-form");
    	
    	// role choice 
	    StringList roleItems = new StringList();
	    UISelect roleSelect = UISelect.make(sameRoleForm, "select-roles", null,
		        "#{siteAddParticipantHandler.sameRoleChoice}", "sameRole");
	    List<Role> roles = handler.getRoles();
	    for (int i = 0; i < roles.size(); ++i) {
	    	Role r = roles.get(i);
		      UIBranchContainer roleRow = UIBranchContainer.make(sameRoleForm,
		          "role-row:", Integer.toString(i));
            UIOutput.make(roleRow, "role-label", r.getId() + " (" + r.getDescription() + ")");
            UISelectChoice.make(roleRow, "role-select", roleSelect.getFullID(), i);
            roleItems.add(r.getId());
        }
        roleSelect.optionlist.setValue(roleItems.toStringArray()); 
        
        // list of users
        for (Iterator<User> it=handler.getUsers().iterator(); it.hasNext(); ) {
        	User user = it.next();
            UIBranchContainer userrow = UIBranchContainer.make(sameRoleForm, "user-row:", user.getId());
            UIMessage message = UIMessage.make(userrow,"user-label","user_tooltip", new String[] {user.getEid() + "(" + user.getSortName() + ")"});
        }
    	
    	UICommand.make(sameRoleForm, "continue", messageLocator.getMessage("gen.continue"), "#{siteAddParticipantHandler.processSameRoleContinue}");
    	UICommand.make(sameRoleForm, "back", messageLocator.getMessage("gen.back"), "#{siteAddParticipantHandler.processSameRoleBack}");
    	UICommand.make(sameRoleForm, "cancel", messageLocator.getMessage("gen.cancel"), "#{siteAddParticipantHandler.processCancel}");
   
         
    }
    
    public ViewParameters getViewParameters() {
    	AddViewParameters params = new AddViewParameters();

        params.id = null;
        return params;
    }
    
    public List reportNavigationCases() {
        List togo = new ArrayList();
        togo.add(new NavigationCase("continue", new SimpleViewParameters(EmailNotiProducer.VIEW_ID)));
        togo.add(new NavigationCase("back", new SimpleViewParameters(AddProducer.VIEW_ID)));
        return togo;
    }
    
    public void interceptActionResult(ARIResult result, ViewParameters incoming,
            Object actionReturn) 
    {
        if ("done".equals(actionReturn)) {
          Tool tool = handler.getCurrentTool();
           result.resultingView = new RawViewParameters(SakaiURLUtil.getHelperDoneURL(tool, sessionManager));
        }
    }
}
