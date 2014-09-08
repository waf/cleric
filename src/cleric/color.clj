(ns cleric.color)

(def colorcode {:white "00"
                :black "01"
                :blue "02"
                :green "03"
                :red "04"
                :brown "05"
                :purple "06"
                :orange "07"
                :yellow "08"
                :lime "09"
                :teal "10"
                :cyan "11"
                :light-blue "12"
                :pink "13"
                :grey "14"
                :silver "15"
                :noop nil})

(def colored "\u0003")
(def reset "\u000F")

(defn color [&{:keys [fg bg]
               :or [fg :noop bg :noop]}]
  (let [fgcolor (colorcode fg)
        bgcolor (colorcode bg)]
    (if fgcolor
      (str colored fgcolor 
           (if bgcolor (str "," bgcolor)))
      reset)))

