var essSupply = angular.module('essSupply');
essSupply.service('SupplyCookieService', ['$cookies', 'appProps', supplyCookieService]);


function supplyCookieService($cookies,appProps) {
    var userId = appProps.user.employeeId;
    /* Cart -> String*/
    function serializatizeCart (cart) {
        return JSON.stringify(cart);
    }

    /*String -> Cart*/
    function deserializeCart  (json) {
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
            var code = encodeCart(serializatizeCart(cart));
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
                return deserializeCart(decodeCart(cur));
        }
    }
};