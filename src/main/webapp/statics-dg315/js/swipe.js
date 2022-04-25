var swiper = new Swiper(".mySwiper", {
    pagination: {
        el: ".swiper-pagination",
        clickable: true,
    },
    loop: true,
    on: {
        slideChange: function () {
            console.log(this.activeIndex)
            let allItems = [...document.querySelectorAll('.desc-item')]
            allItems.forEach((one, index) => {

                if (index !== this.activeIndex - 1) {
                    one.classList.remove('active')
                } else  {
                    one.classList.add('active')
                }

            })
            if (this.activeIndex > allItems.length) {
                allItems[0].classList.add('active')
            }
        },
    },
    autoplay: {
        delay: 2500,
        disableOnInteraction: false
    }
});