<?php
/**
 * Plugin Name: Force SSL for REST (dev only)
 * Description: WooCommerce only honours HTTP Basic Auth when is_ssl() is true.
 *              On this local non-SSL dev site we mark REST requests as "secure"
 *              so the Authorization: Basic ck:cs header from the IIS backend is
 *              accepted. DO NOT use this on a real/public site.
 */

if ( isset( $_SERVER['REQUEST_URI'] ) && strpos( $_SERVER['REQUEST_URI'], '/wp-json/' ) === 0 ) {
    $_SERVER['HTTPS'] = 'on';
}
