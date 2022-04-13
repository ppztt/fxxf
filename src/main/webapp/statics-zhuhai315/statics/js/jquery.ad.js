/* 
*   jQuery Plugins imgFloat v1011 
* 使用说明： 
*   speed //元素移动速度，数值越大速度越慢
*   xPos  //元素一开始左距离 
*   yPos  //元素一开始上距离 
*   $('#div1').imgFloat({speed:30,xPos:10,yPos:10});   
*   $('#div2').imgFloat();   //不给参数默认（speed:10,xPos:0,yPos:0）                      
*/
(function($) { 
    jQuery.fn.imgFloat = function(options) { 
        var own = this; 
        var xD = 0; 
        var yD = 0; 
        var i = 1; 
        var settings = { 
            speed: 10, 
            xPos: 0, 
            yPos: 0 
        }; 
        jQuery.extend(settings, options); 
        var ownTop = settings.xPos; 
        var ownLeft = settings.yPos; 
        own.css({ 
            position: "absolute", 
            cursor: "pointer" 
        }); 
        function imgPosition() { 
            var winWidth = $(window).width() - own.width(); 
            var winHeight = $(window).height() - own.height(); 
            if (xD == 0) { 
                ownLeft += i; 
                own.css({ 
                    left: ownLeft 
                }); 
                if (ownLeft >= winWidth) { 
                    ownLeft = winWidth; 
                    xD = 1; 
                } 
            } 
            if (xD == 1) { 
                ownLeft -= i; 
                own.css({ 
                    left: ownLeft 
                }); 
                if (ownLeft <= 0) xD = 0; 
            } 
            if (yD == 0) { 
                ownTop += i; 
                own.css({ 
                    top: ownTop 
                }); 
                if (ownTop >= winHeight) { 
                    ownTop = winHeight; 
                    yD = 1; 
                } 
            } 
            if (yD == 1) { 
                ownTop -= i; 
                own.css({ 
                    top: ownTop 
                }); 
                if (ownTop <= 0) yD = 0; 
            } 
        } 
        var imgHover = setInterval(imgPosition, settings.speed); 
        own.hover(function() { 
            clearInterval(imgHover); 
        }, 
        function() { 
            imgHover = setInterval(imgPosition, settings.speed); 
        }); 
    } 

/* 
*   jQuery Plugins duiLian v1011 
* 使用说明： 
*   left //是为左联，否为右联
*   xPos  //元素一开始左距离 
*   yPos  //元素一开始上距离
*   speed //执行间隔
*   $('#div1').duiLian({left:false,xPos:10,yPos: 10,speed: 500});   
*   $('#div2').duiLian();   //不给参数默认（left:true,xPos:10,yPos: 10,speed: 500）                      
*/
$.fn.duiLian = function(options) {
        var defaults = {
            left: true,
            xPos: 10,
            yPos: 10,
            speed: 500
        };
        var options = $.extend(defaults, options);
        var othis = this;
        var thisobj = $(this);
        var timerhandle = 0;
        var isleft = options.left;
        var xoffset = options.xPos;
        var yoffset = options.yPos;
        var timeinterval = options.speed;
        //$(this).css("display", "none");
        this.xs = 0;
        this.ys = 0;
        if (this.left == true) {
            this.xs = xoffset;
        }
        else {
            this.xs = $(document).width() - xoffset - $(this).width();
        }
        this.ys = this.yoffset;
        this.f_move = function() {
            this.ys = $(document).scrollTop() + yoffset;
            thisobj.css("position", "absolute");
            thisobj.css("left", this.xs + "px");
            thisobj.css("top", this.ys + "px");
        }
        this.f_start = function() {
            timerhandle = window.setInterval(function() { othis.f_move(); }, options.speed);
        }
        this.f_stop = function() {
            window.clearInterval(timerhandle);
            timerhandle = 0;
        }
        $(this).mouseenter(function() {
            othis.f_stop();
        }).mouseleave(function() {
            othis.f_start();
        });
        othis.f_start();
    };
    $.fn.Show = function() {
        if ($(this).IsDisplay() == false) {
            $(this).css("display", "block");
        }
    }
    $.fn.Hidden = function() {
        $(this).css("display", "none");
    }
    $.fn.IsDisplay = function() {
        return ($(this).css("display") == "none" ? false : true);
    }
})(jQuery);
