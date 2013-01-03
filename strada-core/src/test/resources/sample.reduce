function(id, values) {
    var result = {
      ts     : {},
      total  : 0,
      unique : 0,
      repeat : 0,
      first  : 0,
      action : {},
      dummy  : ${dummy}
    };
    
    values.forEach( function(v) {
      result.ts = v.ts;
      
      result.total  += v.total;
      result.unique += 1;
      if(v.total > 1)  result.repeat += 1;
      if(v.total == 1) result.first  += 1;
      
      for (var key in v.action) {
        if(result.action[key] == null) result.action[key] = 0;
        result.action[key] += v.action[key];
        
        var rkey = key + "_returning";
        if(result.action[rkey] == null) result.action[rkey] = 0;
        if(v.action[key] > 1) result.action[rkey] += 1;
      }
    });
        
    result.first  = NumberInt(result.first);
    result.total  = NumberInt(result.total);
    result.repeat = NumberInt(result.repeat);

    return result;
};
