package t;

import lombok.Setter;
import org.una.navigatetrack.manager.NodesDrawerManager;

@Setter
public abstract class BaseMenuController {
    protected BaseController baseController;
    protected NodesDrawerManager manager;

    public abstract void setupMenu();
}

