function findCapicuaPhones() {
    var cursor = db.phones.find({});
    var capicuaNumbers = [];

    cursor.forEach(function(phone) {
        var phoneNumber = phone._id.toString();
        var reversedNumber = phoneNumber.split("").reverse().join("");
        if (phoneNumber === reversedNumber) {
            capicuaNumbers.push(phone.display);
        }
    });

    if (capicuaNumbers.length > 0) {
        print("Capicuas: " + capicuaNumbers);
    } else {
        print("Capicuas nao encontradas.");
    }
}
