function() {

    function weekDate(d) {
      var nd = new Date(d.valueOf() - d.getDay()*86400000);
      nd.setHours(0);
      return nd;
    };
    
    function mix(obj, key) {
      if(obj[key] == null) obj[key] = 0;
      obj[key] = 1;
    };
    
    var id = {
        oid : this._id.oid,
        d   : ${date}
    };
    
    var values = {
        ts        : this.ts,
        total     : this.hits,
        actions   : {},
        actbrowser: {},
        country   : {},
        os        : this.os,
        browser   : this.browser,
        version   : this.browser_version,
        
        // hourly
        unique : 1,
        repeat : 0,
        first  : 1,
        
        // more time frames
        daily  : this.daily,
        weekly : this.weekly
    };
    
    id.oid = id.oid.substring(0, id.oid.indexOf("_"));
    
    // prepare actions maps
    for (var key in this.actions) {        
      if(values.actions[key] == null) values.actions[key] = 0;
      values.actions[key] = 1;
      
      // actions by country, nested in action
      var country = this.actions[key].country;
      if(values.country[country] == null) values.country[country] = {};
      mix(values.country[country], key);
      
      // mix dimensions
      // actions by browser
      for(var bk in this.browser) {
        if(values.actbrowser[bk] == null) values.actbrowser[bk] = {};
        mix(values.actbrowser[bk], key);
      }
    }
    
    emit(id, values);
};
