function() {
    
    var id = {
        oid : this._id.oid,
        d   : ${date}
    };
    
    var values = {
        ts     : this.ts,
        total  : this.hits,
        unique : 1,
        repeat : 0,
        first  : 1,
        action : this.action
    };
    
    id.oid = id.oid.substring(0, id.oid.indexOf("_"));
    
    emit(id, values);
};