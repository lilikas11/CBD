countByPrefix = function countByPrefix() {
    c = db.phones.aggregate([{$group: {_id: "$components.prefix", count: { $sum: 1 }}}]);
    while (c.hasNext()){
        printjson(c.next());
    }
}