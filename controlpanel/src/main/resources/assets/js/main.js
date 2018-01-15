'use strict';

(function() {
    // set active menu item based on currenr url
    $('a[href="' + window.location.pathname + '"]').parent().addClass('active');
})();
