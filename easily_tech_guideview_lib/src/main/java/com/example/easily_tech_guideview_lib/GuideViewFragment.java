package com.example.easily_tech_guideview_lib;

import ohos.agp.colors.RgbColor;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.LayoutScatter;
import ohos.agp.components.StackLayout;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Color;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.service.Window;
import ohos.app.Context;
import ohos.bundle.BundleInfo;
import ohos.utils.PacMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static ohos.agp.utils.Color.TRANSPARENT;

public class GuideViewFragment extends CommonDialog {
    private static Context context;
    private List<GuideViewBundle> guideViewBundles = new ArrayList<>();
    private StackLayout flContainer;
    private GuideViewBundle currentBundle;
    private GuideView currentGuideView;
    private boolean isShowing;

    public GuideViewFragment(Context context) {
        super(context);
        this.context = context;
    }

    //@Override
    public void onCreate(@Nullable BundleInfo savedInstanceState) {
        super.onCreate();
        setTitleSubText("Set some title");
    }

    @Nullable
    //@Override
    public Component onCreateView(@NotNull LayoutScatter scatter,
                                  @Nullable ComponentContainer container,
                                  @Nullable PacMap savedInstanceState) {
//        flContainer = (StackLayout) scatter;
        return null;
    }

    //@Override
    public void onStart() {
        super.onCreate();
        CommonDialog commonDialog = new CommonDialog(currentGuideView.getContext());
        commonDialog.setTitleText("CommonDialog");
        Window window = commonDialog.getWindow();
        if (window == null) {
            return;
        }
        window.setWindowLayout(ComponentContainer.LayoutConfig.MATCH_PARENT, ComponentContainer.LayoutConfig.MATCH_PARENT);
        window.setBackgroundColor(new RgbColor(Color.TRANSPARENT.getValue()));
        if (!isShowing) {
            isShowing = true;
            show();
        }
    }

    public void setGuideViewBundles(List<GuideViewBundle> guideViewBundles) {
        if (guideViewBundles == null || guideViewBundles.isEmpty()) {
            return;
        }
        this.guideViewBundles = guideViewBundles;
    }

    public void onNext() {
        show();
    }

    public boolean hasNext() {
        return guideViewBundles != null && !guideViewBundles.isEmpty();
    }

    private void showGuideView() {
        if (currentGuideView != null && currentGuideView.isShowing) {
            ShapeElement shapeElement = new ShapeElement();
            if (currentBundle == null) {
                shapeElement.setRgbColor(new RgbColor(TRANSPARENT.getValue()));
            } else {
                shapeElement.setRgbColor(RgbColor.fromArgbInt(currentBundle.getMaskColor()));
            }
            flContainer.setBackground(shapeElement);
        }
        currentGuideView.setVisibility(2);

        do {
            if (guideViewBundles == null || guideViewBundles.isEmpty()) {
                currentBundle = null;
            } else {
                currentBundle = guideViewBundles.remove(0);
            }
        }
        while (currentBundle != null && !currentBundle.condition());
        if (currentBundle == null) {
            //dismiss();
            return;
        }
        GuideView guideView = new GuideView((currentGuideView.getContext()), currentBundle);
        guideView.setTargetViewClickListener(new GuideView.TargetViewClickListener() {
            @Override
            public void onGuideViewClicked() {
                if (currentBundle != null && currentBundle.isDismissOnTouchInTargetView()) {
                    onNext();
                }
            }
        });
        flContainer.addComponent(guideView);
        guideView.show();
        currentGuideView = guideView;
    }

    private void wrapClickListener(Component guideView) {
        if (currentBundle == null || !currentBundle.isDismissOnClicked()) {
            return;
        }
        guideView.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                onNext();
            }
        });
    }
//
//    @Override
//    public void dismiss() {
//        if (getButtonComponent().getContext() instanceof Ability && !((Ability) getButtonComponent().getContext()).isTerminating() && get != null && getDialog) {
//            if (flContainer != null) {
//                flContainer.removeAllComponents();
//                currentBundle = null;
//                currentGuideView = null;
//            }
//            isShowing = false;
//            super.onWindowSelectionUpdated(false);
//        }
//    }

    public static class Builder {
        private List<GuideViewBundle> guideViewBundles = new ArrayList<>();
        private boolean cancelable;

        public Builder addGuideViewBundle(GuideViewBundle bundle) {
            if (bundle == null) {
                return this;
            }
            guideViewBundles.add(bundle);
            return this;
        }

        public Builder setCancelable(boolean Cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public GuideViewFragment build() {
            GuideViewFragment fragment = new GuideViewFragment(context);
            fragment.setGuideViewBundles(guideViewBundles);
            fragment.siteRemovable(cancelable);
            return fragment;
        }
    }
}
