package net.bonysoft.magicmirror.modules;

import java.util.List;

public class DashboardModuleComposite implements DashboardModule {

    private final List<DashboardModule> modules;

    public DashboardModuleComposite(List<DashboardModule> modules) {
        this.modules = modules;
    }

    @Override
    public void update() {
        for (DashboardModule module : modules) {
            module.update();
        }
    }

    @Override
    public void stop() {
        for (DashboardModule module : modules) {
            module.stop();
        }
    }

}
