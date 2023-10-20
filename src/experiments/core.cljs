(ns experiments.core
  (:require
    [promesa.core :as p]
    [applied-science.js-interop :as j]
    [shadow.resource :as rc]
    [reagent.core :as r]
    [reagent.dom :as rdom]
    ["soundfont2" :refer [SoundFont2]]
    [dopestyle.core :refer [component-footer inline-img]]))

(defonce state (r/atom {}))

(defn component-header []
  [:header
   [:a {:href "/"}
    [inline-img (rc/inline "style/img/logo.svg") {:class "logo"}]
    [:h2.fat {:title "Experiments"} "Experiments"]]
   [:nav
    [:a {:href "/auth/sign-in"} "Sign in"]]])

(defn component-main [state]
  [:<>
   [component-header]
   [:main
    (doall
      (let [{:keys [sf2]} @state
            banks (aget sf2 "banks")]
        (for [b (range (count banks))]
          (let [bank (nth banks b)]
            (when bank
              [:ul {:key b}
               [:h3 "Bank #" b]
               (for [preset (aget bank "presets")]
                 (when preset
                   [:li
                    {:key (aget preset "header" "preset")
                     :on-click #(js/console.log
                                  (j/call sf2 :getKeyData
                                          60
                                          (aget preset "bank")
                                          (aget preset "preset")))}
                    (aget preset "header" "preset") " "
                    (aget preset "header" "name")]))])))))]
   [component-footer]])

(defn start {:dev/after-load true} []
  (rdom/render [component-main state]
                 (js/document.getElementById "app")))

(defn init []
  (p/let [req (js/fetch "fluidr3_gm.sf2")
          buffer (.arrayBuffer req)
          sf2-data (js/Uint8Array. buffer)
          sf2 (SoundFont2. sf2-data)]
    (swap! state assoc :sf2 sf2)
    (start)))
