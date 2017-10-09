/**
 * Created by christoferdutz on 23.03.16.
 */
package de.codecentric.iot.rapiro.settings {
public class SettingsService {
    
    private var _selectedLanguage:String;
    
    public function SettingsService() {
    }
    
    public function get selectedLanguage():String {
        return _selectedLanguage;
    }

    public function set selectedLanguage(value:String):void {
        _selectedLanguage = value;
    }
}
}
