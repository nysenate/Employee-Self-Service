(function () {

    var ctxPath = 'https://goo.gl';

    var canvas = document.getElementById('dbug-canvas');
    if (!canvas) {
        return;
    }
    var ctx = canvas.getContext("2d");

    var requestAnimationFrame = getRequestAnimationFrameFn();

    var width = document.body.clientWidth;
    var height = document.body.clientHeight;

    var then = Date.now();

    var sprites = {};

    var bugConst = {
        pfx: 'bug',
        idNo: 0,
        img: ctxPath + '/7p9rz3',
        scale: 0.1,
        speed: 500,
        sight: 300
    };

    var bugs = [];

    canvas.width = width;
    canvas.height = height;

    var mouse = {x: 0, y: 0};
    canvas.addEventListener('mousemove', function (e) {
        var rect = canvas.getBoundingClientRect();
        mouse = {x: e.clientX - rect.left, y: e.clientY - rect.top};
    });

    var mouseClicked = false;
    canvas.addEventListener('click', function (e) {
        mouseClicked = true;
    });

    function initializeSprite(name, imgSrc, posX, posY, rotation, scale) {
        var sprite = {
            name: name,
            img: new Image(),
            x: posX || 0,
            y: posY || 0,
            rotation: rotation || 0,
            scale: scale || 1
        };
        sprite.img.src = imgSrc;
        sprites[name] = sprite;
        return sprite;
    }

    function initializeBug(startX, startY, size, speed) {
        var name = bugConst.pfx + bugConst.idNo++;
        var scale = bugConst.scale * (size || 1);
        var rotation = Math.random() * 2 * Math.PI;
        var bugSprite = initializeSprite(name, bugConst.img, startX, startY, rotation, scale);
        bugSprite.speed = bugConst.speed * (speed || 1);
        bugs.push(bugSprite);
        return bugSprite;
    }

    function initializeRandomBug() {
        var startX = Math.random() * width,
            startY = Math.random() * height;
        return initializeBug(startX, startY);
    }

    function imageLoaded(img) {
        return img.complete && img.naturalWidth > 0;
    }

    function getRequestAnimationFrameFn() {
        var w = window;
        return w.requestAnimationFrame
            || w.webkitRequestAnimationFrame
            || w.msRequestAnimationFrame
            || w.mozRequestAnimationFrame;
    }

    function drawRotatedImage(image, x, y, scale, angle) {

        ctx.save();
        ctx.translate(x, y);
        ctx.rotate(angle);
        var scaledDim = {width: image.width * scale, height: image.height * scale};
        ctx.drawImage(image, -(scaledDim.width / 2), -(scaledDim.height / 2),
                                scaledDim.width, scaledDim.height);
        ctx.restore();
    }

    function renderSprite(sprite) {
        if (imageLoaded(sprite.img)) {
            drawRotatedImage(sprite.img, sprite.x, sprite.y, sprite.scale, sprite.rotation);
        }
    }

    function render() {
        ctx.clearRect(0, 0, width, height);
        for (var iSprite in sprites) {
            if (sprites.hasOwnProperty(iSprite)) {
                renderSprite(sprites[iSprite]);
            }
        }
    }

    function mouseOnBug(bug) {
        var hitboxMargin = 50;

        var bugRadiusVect = {
            x: bug.img.width * bug.scale / 2,
            y: bug.img.height * bug.scale / 2
        };

        var bugRadius = getDistance(bugRadiusVect);

        var distanceFromBug = getDistance(getMovementVector(bug, mouse));

        return distanceFromBug < (bugRadius + hitboxMargin);
    }

    function getDistance(movementVector) {
        return Math.sqrt(Math.pow(movementVector.x, 2) + Math.pow(movementVector.y, 2));
    }

    function getDirectionVector(movementVector) {
        var dist = getDistance(movementVector);
        return {
            x: movementVector.x / dist,
            y: movementVector.y / dist
        }
    }

    // Get vector to move from p1 -> p2
    // Assuming that world is finite rectangle with wrapping edges
    function getMovementVector(p1, p2) {
        var mov = {
            x: p2.x - p1.x,
            y: p2.y - p1.y
        };
        if (Math.abs(mov.x) > width / 2) {
            mov.x = -1 * Math.sign(mov.x) * (width - Math.abs(mov.x));
        }
        if (Math.abs(mov.y) > height / 2) {
            mov.y = -1 * Math.sign(mov.y) * (height - Math.abs(mov.y));
        }
        return mov;
    }

    function adjustCoordinate(coord, span) {
        if (coord < 0) {
            return span + coord % span;
        }
        return coord % span;
    }

    function moveSprite(sprite, direction, distance) {
        sprite.x = adjustCoordinate(sprite.x + direction.x * distance, width);
        sprite.y = adjustCoordinate(sprite.y + direction.y * distance, height);

        var offset = Math.PI/2;
        sprite.rotation = offset + Math.atan2(direction.y, direction.x);
    }

    function updateBug(bug, modifier) {
        var movementVector = getMovementVector(mouse, bug);
        var directionVector = getDirectionVector(movementVector);
        var dist = getDistance(movementVector);
        if (dist < bugConst.sight) {
            moveSprite(bug, directionVector, bug.speed * modifier);
        }
    }

    function clickBug(bug) {
        console.log('bug clicked', bug);
        initializeRandomBug();
    }

    function onMouseClick() {
        for (var iBug = 0; iBug < bugs.length; iBug++) {
            var bug = bugs[iBug];
            if (mouseOnBug(bug)) {
                clickBug(bug);
            }
        }
    }

    function update(modifier) {
        if (mouseClicked) {
            onMouseClick();
            mouseClicked = false;
        }
        for (var iBug = 0; iBug < bugs.length; iBug++) {
            updateBug(bugs[iBug], modifier);
        }
    }

    function main() {
        var now = Date.now();
        var delta = now - then;
        then = now;
        var secondsDelta = delta / 1000;

        update(secondsDelta);
        render();

        requestAnimationFrame(main);
    }

    initializeBug(width/2, height/2, 1, 1);
    main();

})();

