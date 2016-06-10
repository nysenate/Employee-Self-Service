var essSupply = angular.module('essSupply');
essSupply.service('SupplyCookieService', ['$cookies', 'appProps', supplyCookieService]);


function supplyCookieService($cookies,appProps) {
    var userId = appProps.user.employeeId;
    /* Cart -> String*/
    function serializatize (cart) {
        return JSON.stringify(cart);
    }

    /*String -> Cart*/
    function deserialize(json) {
        return JSON.parse(json);
    }

    /*serialized cart -> encoded string*/
    function  encodeCart  (cart) {
        return btoa(cart);
    }

    /*encoded string -> serialized cart*/
    function decodeCart (s) {
        return atob(s);
    }
    
    return {
        /*() ->add user Cart*/
        addCart: function (cart) {
            var code = encodeCart(serializatize(cart));
            var cur = $cookies.get(userId);
            if (cur != null || cur != undefined)
               $cookies.remove(userId);
            $cookies.put(userId, code);
        },

        /*user -> Cart*/
        getCart: function () {
            var cur = $cookies.get(userId);
            if (cur == null || cur == undefined)
                return [];
            else
                return deserialize(decodeCart(cur));
        },
        resetCart:function () {
            var cur = $cookies.get(userId);
                if (cur != null || cur != undefined)
                    $cookies.remove(userId);
        },
        addDestination: function (dist) {
            var key = "destination";
            var cur = $cookies.get(key);
            if (cur != null || cur != undefined)
                $cookies.remove(key);
            $cookies.put(key, serializatize(dist));
        },
        getDestination: function () {
            var key = "destination";
            var cur = $cookies.get(key);
            if (cur == null || cur == undefined)
                return [];
            else 
                return deserialize(cur);
        },
        resetDestination:function () {
            var key = "destination";
            var cur = $cookies.get(key);
            if (cur != null || cur != undefined)
                $cookies.remove(key);
        },
    }
};