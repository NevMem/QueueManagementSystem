import EnglishDictionary from './EnglishDictionary'
import RussianDictionary from './RussianDictionary'
import englishLanguageImage from '../images/en_lang_image.svg'
import russianLanguageImage from '../images/ru_lang_image.svg'

class Language {
    constructor(name, icon, id) {
        this.name = name
        this.icon = icon
        this.id = id
    }
}

const english = new Language('English', englishLanguageImage, 'en')
const russian = new Language('Русский', russianLanguageImage, 'ru')

const currentLangStorageKey = 'currentLanguage'

class LocalizationService {
    constructor() {
        this.localizationMap = new Map()

        this.availableLanguages = []

        this.addLocalization(english, EnglishDictionary)
        this.addLocalization(russian, RussianDictionary)

        this.curLanguage = english
        this.defautLanguage = russian

        for (const lang of this.availableLanguages) {
            if (lang.id === localStorage.getItem(currentLangStorageKey)) {
                this.curLanguage = lang
            }
        }
    }

    languages() {
        return this.availableLanguages
    }

    currentLanguage() {
        return this.curLanguage
    }

    setCurrentLanguage(language) {
        if (this.curLanguage != language) {
            this.curLanguage = language
            localStorage.setItem(currentLangStorageKey, this.curLanguage.id)
            window.location.reload()
        }
    }

    addLocalization(lang, dict) {
        this.availableLanguages.push(lang)
        for (const id of Object.keys(dict)) {
            if (this.localizationMap.has(id)) {
                this.localizationMap.get(id)[lang.id] = dict[id]
            } else {
                this.localizationMap.set(id, {[lang.id]: dict[id]})
            }
        }
    }

    localizedString(stringId) {
        const langId = this.curLanguage.id
        if (!this.localizationMap.has(stringId)) {
            return stringId
        }
        const variants = this.localizationMap.get(stringId)
        if (langId in variants) {
            return variants[langId]
        }
        if (this.defautLanguage.id in variants) {
            return variants[this.defautLanguage.id]
        }
        return stringId
    }
}

const service = new LocalizationService()
export default service
