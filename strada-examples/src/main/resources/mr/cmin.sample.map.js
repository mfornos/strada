function() {

    function weekDate(d) {
      var nd = new Date(d.valueOf() - d.getDay()*86400000);
      nd.setHours(0);
      return nd;
    };
    
    var id = {
        oid : this._id.oid,
        d   : ${date}
    };
    
    var values = {
        ts     : this.ts,
        total  : this.hits,
        actions: {},
        country: {},
        os     : this.os,
        browser: this.browser,
        version: this.browser_version,
        
        // hourly
        unique : 1,
        repeat : 0,
        first  : 1,
        
        // more time frames
        daily  : this.daily,
        weekly : this.weekly
    };
    
    id.oid = id.oid.substring(0, id.oid.indexOf("_"));
    
    for (var key in this.actions) {        
      if(values.actions[key] == null) values.actions[key] = 0;
      values.actions[key] = 1;
      
      var country = this.actions[key].country;
      
    	  if(values.country[country] == null) values.country[country] = {};
    	  var vk = values.country[country];
    	  // action
    	  if(vk[key] == null) vk[key] = 0;
          vk[key] = 1;

    }
    
    emit(id, values);
};
