package com.zephsie.report.utils.views;

public interface EntityView {
    interface Base {
    }

    interface System extends Base {
    }

    interface WithMappings extends System {
    }

    interface Full extends WithMappings {
    }
}
