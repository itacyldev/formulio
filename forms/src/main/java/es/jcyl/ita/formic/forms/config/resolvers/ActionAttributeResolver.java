package es.jcyl.ita.formic.forms.config.resolvers;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.mini2Dx.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.actions.ActionType;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.config.reader.ReadingProcessListener;
import es.jcyl.ita.formic.forms.controllers.FormController;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.controllers.UIParam;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.project.Project;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;
import static es.jcyl.ita.formic.forms.config.DevConsole.info;

/**
 * Resolver in charge of creation of UIAction based on value of the attribute "action".
 * The action resolution/creation follow this rules:
 * <ul>
 *     <li>If the "action" attribute is set, it can refer to:
 *     <ol>
 *         <li>A predefined action (add/del/nav/js/...)In this case, a ConfigNode is created for the
 *         action, so the ActionBuilder will pick it up later to create the UIAction element and
 *         assign it to current node.</li>
 *         <li>A custom action defined in the tag <actions/> of current formController (view). In
 *         this case, after all the view is processed, this resolver will try to match the "action"
 *         attribute value with the id of one the formController actions. If an action is found, it
 *         is cloned and assigned to current element. If there are nested UIParam in current element
 *         they will override (add/replace) the formController action parameters.</li>
 *         <li>A js method (add/del/nav/js/...). If in step 2, no action no matching action is found
 *         in the controller, it is assummed that the value of attribute "action" will refer to a
 *         js method, so the corresponding function must exist in the <script> tag in current view,
 *         so the resolver checks that a <script> element is defined in current view.
 *     </ol>
 *     </li>
 *     <li>If the action attribute is set, no nested action can be defined.</li>
 *     <li>If the action attribute is set, there may exist nested UIParam elements representing
 *     the parameters of the action or parameters to override (add/replace) custom action parameters.
 *     </li>
 * </ul>
 * Apart from the creation of ConfigNode to create nested acionts, when an element processing is
 * finished, this resolver is notified though the ReadingProcessListener and it sets the action to
 * the component.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ActionAttributeResolver extends AbstractAttributeResolver implements ReadingProcessListener {


    private List<ConfigNode> unresolvedActions = new ArrayList<>();

    @Override
    public Object resolve(ConfigNode node, String attName) {
        String actionType = node.getAttribute(attName);
        // check if threre's a nested <action/> element
        ConfigNode nestedAction = ConfigNodeHelper.getFirstChildrenByTag(node, "action");
        if (nestedAction != null) {
            throw new ConfigurationException(
                    error(String.format("Error in component <%s id='%s'>. If you set the attribute" +
                                    " 'type' to use a custom or predefined action, you cannot define " +
                                    "a nested action. If you're trying to redefine the [%s] action " +
                                    "parameters, just leave the <param/> tags within this component " +
                                    "and remove <action/>.  in file '${file}'.",
                            node.getName(), node.getId(), actionType)));
        }
        // check if the actionType is referring to a predefined action
        try {
            ActionType actTypeEnum = ActionType.valueOf(actionType.toUpperCase());
            createActionNode(actTypeEnum, node);
        } catch (Exception e) {
            // it is not a predefined action, it has to be a js action or a custom action
            registerUnresolvedAction(node);
        }
        // the action will be set by the actionBuilder
        return null;
    }

    /**
     * Creates a nested config node for the 'action' attributed so the ActionBuilder will pick up
     * the node and create the action
     *
     * @param actionType
     * @param node
     */
    private void createActionNode(ActionType actionType, ConfigNode node) {
        ConfigNode actionNode = new ConfigNode("action");
        actionNode.setAttribute("type", actionType.name().toLowerCase());
        // if node has "param" nested nodes, assign them to the action
        List<ConfigNode> paramNodes = ConfigNodeHelper.getDescendantByTag(node, "param");
        for (ConfigNode pNode : paramNodes) {
            pNode.setParent(actionNode);
            actionNode.addChild(pNode);
        }
        // add action conf node to current component node
        node.addChild(actionNode);
    }

    /**
     * If the referenced action is not a predefined one, register it as unresolved and at the end
     * of current this view, the all actions has been declared, try to resolve it.
     *
     * @param node
     */
    private void registerUnresolvedAction(ConfigNode node) {
        unresolvedActions.add(node);
    }


    /*********************/
    /** ReadingProcessListener Interface */
    /*********************/

    @Override
    public void fileStart(String currentFile) {
    }

    @Override
    public void fileEnd(String currentFile) {
    }

    @Override
    public void viewStart(ConfigNode node) {
        unresolvedActions.clear();
    }

    @Override
    public void viewEnd(ConfigNode viewNode) {
        // try to resolve pending actions, access to current controller to find current view
        // defined actions and if the actions exists, copy it and merge parameters
        // TODO: remove list/edit differences and use view as formController related element
//        UIView view = (UIView) viewNode.getElement();
        FormController formController = (FormController) viewNode.getElement();

        Map<String, UIAction> actionMap = formController.getActionMap();

        String actionName;
        for (ConfigNode componentNode : unresolvedActions) {
            actionName = componentNode.getAttribute("action");
            // check if exists in current controller actions
            boolean noActionFound = (actionMap == null) || !actionMap.containsKey(actionName);
            if (noActionFound) {
                if (!checkExistsScript(viewNode)) {
                    throw new ConfigurationException(error(String.format("No custom action with id " +
                            "[%s] defined in controller [%s] and there's no <script> tag " +
                            "neither in this controller, so check the 'action' attribute " +
                            "refers to a custom action in <actions> or a js method in " +
                            "<script>.", actionName, formController.getId())));
                } else {
                    info(String.format("No defined action with name [%s] in controller [%s]. Make sure " +
                            "there's a js function with this name.", actionName, formController.getId()));
                    setupJsAction(componentNode);
                }
            } else {
                UIAction uiAction = actionMap.get(actionName);
                setupControllerAction(componentNode, uiAction);
            }
        }
    }

    /**
     * Create a JS action definition for the value of "action" attribute.
     *
     * @param node
     */
    private void setupJsAction(ConfigNode node) {
        UIComponent component = (UIComponent) node.getElement();
        UIAction componentAction = new UIAction();
        componentAction.setType(ActionType.JS.name());
        componentAction.setRegisterInHistory(false);
        componentAction.setForceRefresh(true);

        // create param "method" with the name of the js function
        UIParam[] params = new UIParam[1];
        ValueBindingExpression paramValue = this.factory.getExpressionFactory().create(node.getAttribute("action"));
        params[0] = new UIParam("method", paramValue);
        componentAction.setParams(params);
        // set action to component
        component.setAction(componentAction);
    }

    /**
     * Checks if the passed view node contains a nested element <script/>.
     *
     * @param viewNode
     * @return
     */
    private boolean checkExistsScript(ConfigNode viewNode) {
        return ConfigNodeHelper.hasChildrenByTag(viewNode, "script");
    }

    /**
     * The the passed action to the element node, and if this one already has and action,
     * merges the parameters.
     *
     * @param node
     * @param action
     */
    private void setupControllerAction(ConfigNode node, UIAction action) {
        UIComponent component = (UIComponent) node.getElement();
        // clone action
        UIAction componentAction = new UIAction(action);
        component.setAction(componentAction);
        // check if component has nested parameters, if so, merge them
        List<ConfigNode> componentParams = ConfigNodeHelper.getDescendantByTag(node, "param");
        if (CollectionUtils.isNotEmpty(componentParams)) {
            UIParam[] parameters = mergeParameters(componentAction, componentParams);
            componentAction.setParams(parameters);
        }
    }

    /**
     * Takes the UIParam defined in the paramNodes attributes and merges them with the UIParam
     * assigned to the passed action. Parameters in paramNodes that don't exist in the action will
     * be added, and if they exist, they'll replace the action ones.
     *
     * @param action
     * @param paramNodes
     * @return
     */
    private UIParam[] mergeParameters(UIAction action, List<ConfigNode> paramNodes) {
        List<UIParam> actionParams = Arrays.asList(action.getParams());
        for (ConfigNode node : paramNodes) {
            UIParam param = (UIParam) node.getElement();
            // find the parameter in action, if exists replace, if not add
            boolean found = false;
            int i = 0;
            for (UIParam actionParameter : actionParams) {
                if (actionParameter.getName().equalsIgnoreCase(param.getName())) {
                    // replace
                    actionParams.add(i, param);
                    found = true;
                    break;
                }
                i++;
            }
            if (!found) {
                // add parameter
                actionParams.add(param);
            }
        }
        return actionParams.toArray(new UIParam[actionParams.size()]);
    }

    @Override
    public void elementStart(ConfigNode node) {
    }

    @Override
    public void elementEnd(ConfigNode node) {
        if (!node.hasChildren()) {
            return;
        }
        Object element = node.getElement();
        if (element instanceof UIComponent) {
            // if the element has a nested action set to the UIComponent
            List<ConfigNode> actions = ConfigNodeHelper.getChildrenByTag(node, "action");
            if (CollectionUtils.isNotEmpty(actions)) {
                ((UIComponent) element).setAction((UIAction) actions.get(0).getElement());
            }
        }
    }

    @Override
    public Project getProject() {
        return null;
    }

    @Override
    public void setProject(Project project) {
    }
}
