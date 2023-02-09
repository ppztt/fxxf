function initFirstSwiper() {
    new Swiper('#first-swiper', {
        loop: true,
        autoplay: true,
        pagination: {
            el: '.swiper-pagination',
            clickable: true,
        },
    })
}

function initLastSwiper() {
    new Swiper('#last-swiper', {
        slidesPerView: 3,
        spaceBetween: 30,
        navigation: {
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev',
        },
    })
}

initFirstSwiper();
initLastSwiper();

