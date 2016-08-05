(defproject learn-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories [["jcenter" {:url "http://jcenter.bintray.com"}]]
  :dependencies [
    [org.clojure/clojure "1.8.0"]
    [org.processing/core "2.2.1"]
    [me.lsdo.processing/lsdome-processing "0.0.1"]]
  :main ^:skip-aot learn-clj.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
