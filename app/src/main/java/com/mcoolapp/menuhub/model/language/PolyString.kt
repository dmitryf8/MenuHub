package com.mcoolapp.menuhub.model.language

class PolyString(){
    //класс, который может содержать в себе переводы одного и того же текста на разных языках
    private var languageList: ArrayList<LanguageString> = ArrayList()

    constructor(languageString: LanguageString): this(){
        languageList.add(languageString)
    }

    public fun getLanguageNumber() : Int{
        //возвращает количество добавленных переводов текста
        return languageList.size
    }

    public fun getLanguageList(): List<String>{
        //возвращает список добавленных языков
        val lList: ArrayList<String> = ArrayList()

        for ( s in languageList) {
            lList.add(s.language)
        }
        return lList
    }

    public fun get(language: String) : String{
        //получаем текстовое значение на нужном языке, если такого нет, возвращает Language not found
        var s: String = "Language not found"

        for ( l in languageList) {
            if (language.equals(l)) {
                s = l.text
            }
        }

        return s
    }

    public fun containLanguage(language: String) : Boolean {
        //проверяем наличие текста на языке language
        var b: Boolean = false

        for (l in languageList) {
            if (l.language.equals(language)) b = true
        }

        return b
    }

    public fun addTranslation(languageString: LanguageString) {
        //добавляем перевод languageString, если такой перевод уже есть, обновляем его текст
        var b: Boolean = true
        for (l in languageList)
            if (l.language.equals(languageString.language)) b = false else l.text = languageString.text
        if (b) languageList.add(languageString)
    }

}