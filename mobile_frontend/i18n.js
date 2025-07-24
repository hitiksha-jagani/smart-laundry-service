import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import { Platform } from 'react-native';

import en from './locales/en/translation';
import hi from './locales/hi/translation';


let language = 'en';

if (Platform.OS !== 'web') {
  try {
    const RNLocalize = require('react-native-localize');
    const locales = RNLocalize.getLocales();
    if (Array.isArray(locales) && locales.length > 0) {
      language = locales[0].languageCode;
    }
  } catch (error) {
    console.warn('Failed to load react-native-localize:', error);
  }
}

i18n.use(initReactI18next).init({
  lng: language,
  fallbackLng: 'en',
  resources: {
    en: { translation: en },
    hi: { translation: hi },
  },
  interpolation: {
    escapeValue: false,
  },
});

export default i18n;
