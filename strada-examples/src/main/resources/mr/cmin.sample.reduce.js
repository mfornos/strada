function(id, values) {
    var conversions = ${conversions};
    
    var result = {
      ts     : {},
      total  : 0,
      unique : 0,
      repeat : 0,
      first  : 0,
      daily  : {
        first : 0,
        repeat: 0
      },
      weekly : {
        first : 0,
        repeat: 0
      },
      actions: {},
      country: {},
      os     : {},
      browser: {},
      version: {},
      conversion: {}
    };
    
    function objGroup(from, to) {
      for (var key in from) {
        var val = from[key];
        // Patch for empty obj
        if(typeof val == "number") {
          if(to[key] == null) to[key] = 0;
          to[key] += val;
        }
      }
    };
    
    function contains(a, name){
      for(var i = 0; i < a.length; i++) {
        if(a[i] == name) return true;
      }
      return false;
    };
    
    values.forEach( function(v) {
      result.ts = v.ts;
      
      result.total  += v.total;
      result.unique += 1;
      if(v.total > 1)  result.repeat += 1;
      if(v.total == 1) result.first  += 1;
      
      // Frequencies
      // TODO interpolate in frame dependent template
      
      if(v.daily.freq > 1) result.daily.repeat += 1;
      if(v.daily.freq == 1) result.daily.first += 1;
      
      if(v.weekly.freq > 1) result.weekly.repeat += 1;
      if(v.weekly.freq == 1) result.weekly.first += 1;
      
      // UA
      objGroup(v.os, result.os);
      objGroup(v.browser, result.browser);
      // nested versions
      for (var key in v.version) {
        if(result.version[key] == null) result.version[key] = {};
        objGroup(v.version[key], result.version[key]);
      }
      
      // Actions
      // objGroup(v.action, result.action);
      
      // Conversions
      
     for (var key in v.actions) {
        var val = v.actions[key];
        
        /*
		 * if(contains(conversions, key)) { if(result.conversion[key] == null)
		 * result.conversion[key] = 0; result.conversion[key] += 1; }
		 */
        
        if(typeof val == "number") {
          if(result.actions[key] == null) result.actions[key] = 0;
          result.actions[key] += val;
        }
      }
      
      // Actions by country cohort
     for (var key in v.country) {
         if(result.country[key] == null) result.country[key] = {};
         objGroup(v.country[key], result.country[key]);
     }
        
        // var rkey = key + "_returning";
        // if(result.action[rkey] == null) result.action[rkey] = 0;
        // if(v.action[key] > 1) result.action[rkey] += 1;
      
    });
        
    result.first  = NumberInt(result.first);
    result.total  = NumberInt(result.total);
    result.repeat = NumberInt(result.repeat);

    return result;
};
