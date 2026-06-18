<?php
/**
 * Creates a Read/Write WooCommerce REST API key for the "admin" user and prints
 * the consumer key/secret in application.properties format.
 *
 * WooCommerce normally shows the secret only once in the admin UI; this script
 * generates the pair directly so you can copy it straight into the backend.
 *
 * Run from the woocommerce-dev/ folder:
 *   docker compose run --rm --entrypoint wp wpcli eval-file /scripts/create-api-key.php
 */

if ( ! function_exists( 'wc_rand_hash' ) || ! function_exists( 'wc_api_hash' ) ) {
    WP_CLI::error( 'WooCommerce is not active yet. Run "docker compose up -d" first.' );
}

$user = get_user_by( 'login', 'admin' );
if ( ! $user ) {
    WP_CLI::error( 'The "admin" user was not found.' );
}

global $wpdb;

$consumer_key    = 'ck_' . wc_rand_hash();
$consumer_secret = 'cs_' . wc_rand_hash();

$wpdb->insert(
    $wpdb->prefix . 'woocommerce_api_keys',
    array(
        'user_id'         => $user->ID,
        'description'     => 'IIS dev key (' . gmdate( 'Y-m-d H:i' ) . ')',
        'permissions'     => 'read_write',
        'consumer_key'    => wc_api_hash( $consumer_key ),
        'consumer_secret' => $consumer_secret,
        'truncated_key'   => substr( $consumer_key, -7 ),
    ),
    array( '%d', '%s', '%s', '%s', '%s', '%s' )
);

WP_CLI::line( '' );
WP_CLI::line( '# Copy these into backend/src/main/resources/application.properties:' );
WP_CLI::line( 'app.order-source=woocommerce' );
WP_CLI::line( 'app.woocommerce.base-url=http://localhost:8090' );
WP_CLI::line( 'app.woocommerce.consumer-key=' . $consumer_key );
WP_CLI::line( 'app.woocommerce.consumer-secret=' . $consumer_secret );
WP_CLI::line( '' );
