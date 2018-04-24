(function () {
  'use strict';

  angular.module('s2DashboardFramework')
    .component('editDashboard', {
      templateUrl: 'app/components/edit/editDashboardComponent/edit.dashboard.html',
      controller: EditDashboardController,
      controllerAs: 'ed',
      bindings: {
        "dashboard":"=",
        "public":"&",
        "id":"&",
        "selectedpage" : "&"
      }
    });

  /** @ngInject */
  function EditDashboardController($log, $scope, $mdSidenav, $mdDialog, $mdBottomSheet, sofia2HttpService, interactionService) {
    var ed = this;

    //Gadget source connection type list
    var typeGadgetList = ["pie","bar","map","livehtml","radar","table"];

    ed.$onInit = function () {
      ed.selectedlayer = 0;
      ed.selectedpage = ed.selectedpage();
      ed.icons = [
        "3d_rotation",
        "ac_unit",
        "access_alarm",
        "access_alarms",
        "access_time",
        "accessibility",
        "accessible",
        "account_balance",
        "account_balance_wallet",
        "account_box",
        "account_circle",
        "adb",
        "add",
        "add_a_photo",
        "add_alarm",
        "add_alert",
        "add_box",
        "add_circle",
        "add_circle_outline",
        "add_location",
        "add_shopping_cart",
        "add_to_photos",
        "add_to_queue",
        "adjust",
        "airline_seat_flat",
        "airline_seat_flat_angled",
        "airline_seat_individual_suite",
        "airline_seat_legroom_extra",
        "airline_seat_legroom_normal",
        "airline_seat_legroom_reduced",
        "airline_seat_recline_extra",
        "airline_seat_recline_normal",
        "airplanemode_active",
        "airplanemode_inactive",
        "airplay",
        "airport_shuttle",
        "alarm",
        "alarm_add",
        "alarm_off",
        "alarm_on",
        "album",
        "all_inclusive",
        "all_out",
        "android",
        "announcement",
        "apps",
        "archive",
        "arrow_back",
        "arrow_downward",
        "arrow_drop_down",
        "arrow_drop_down_circle",
        "arrow_drop_up",
        "arrow_forward",
        "arrow_upward",
        "art_track",
        "aspect_ratio",
        "assessment",
        "assignment",
        "assignment_ind",
        "assignment_late",
        "assignment_return",
        "assignment_returned",
        "assignment_turned_in",
        "assistant",
        "assistant_photo",
        "attach_file",
        "attach_money",
        "attachment",
        "audiotrack",
        "autorenew",
        "av_timer",
        "backspace",
        "backup",
        "battery_alert",
        "battery_charging_full",
        "battery_full",
        "battery_std",
        "battery_unknown",
        "beach_access",
        "beenhere",
        "block",
        "bluetooth",
        "bluetooth_audio",
        "bluetooth_connected",
        "bluetooth_disabled",
        "bluetooth_searching",
        "blur_circular",
        "blur_linear",
        "blur_off",
        "blur_on",
        "book",
        "bookmark",
        "bookmark_border",
        "border_all",
        "border_bottom",
        "border_clear",
        "border_color",
        "border_horizontal",
        "border_inner",
        "border_left",
        "border_outer",
        "border_right",
        "border_style",
        "border_top",
        "border_vertical",
        "branding_watermark",
        "brightness_1",
        "brightness_2",
        "brightness_3",
        "brightness_4",
        "brightness_5",
        "brightness_6",
        "brightness_7",
        "brightness_auto",
        "brightness_high",
        "brightness_low",
        "brightness_medium",
        "broken_image",
        "brush",
        "bubble_chart",
        "bug_report",
        "build",
        "burst_mode",
        "business",
        "business_center",
        "cached",
        "cake",
        "call",
        "call_end",
        "call_made",
        "call_merge",
        "call_missed",
        "call_missed_outgoing",
        "call_received",
        "call_split",
        "call_to_action",
        "camera",
        "camera_alt",
        "camera_enhance",
        "camera_front",
        "camera_rear",
        "camera_roll",
        "cancel",
        "card_giftcard",
        "card_membership",
        "card_travel",
        "casino",
        "cast",
        "cast_connected",
        "center_focus_strong",
        "center_focus_weak",
        "change_history",
        "chat",
        "chat_bubble",
        "chat_bubble_outline",
        "check",
        "check_box",
        "check_box_outline_blank",
        "check_circle",
        "chevron_left",
        "chevron_right",
        "child_care",
        "child_friendly",
        "chrome_reader_mode",
        "class",
        "clear",
        "clear_all",
        "close",
        "closed_caption",
        "cloud",
        "cloud_circle",
        "cloud_done",
        "cloud_download",
        "cloud_off",
        "cloud_queue",
        "cloud_upload",
        "code",
        "collections",
        "collections_bookmark",
        "color_lens",
        "colorize",
        "comment",
        "compare",
        "compare_arrows",
        "computer",
        "confirmation_number",
        "contact_mail",
        "contact_phone",
        "contacts",
        "content_copy",
        "content_cut",
        "content_paste",
        "control_point",
        "control_point_duplicate",
        "copyright",
        "create",
        "create_new_folder",
        "credit_card",
        "crop",
        "crop_16_9",
        "crop_3_2",
        "crop_5_4",
        "crop_7_5",
        "crop_din",
        "crop_free",
        "crop_landscape",
        "crop_original",
        "crop_portrait",
        "crop_rotate",
        "crop_square",
        "dashboard",
        "data_usage",
        "date_range",
        "dehaze",
        "delete",
        "delete_forever",
        "delete_sweep",
        "description",
        "desktop_mac",
        "desktop_windows",
        "details",
        "developer_board",
        "developer_mode",
        "device_hub",
        "devices",
        "devices_other",
        "dialer_sip",
        "dialpad",
        "directions",
        "directions_bike",
        "directions_boat",
        "directions_bus",
        "directions_car",
        "directions_railway",
        "directions_run",
        "directions_subway",
        "directions_transit",
        "directions_walk",
        "disc_full",
        "dns",
        "do_not_disturb",
        "do_not_disturb_alt",
        "do_not_disturb_off",
        "do_not_disturb_on",
        "dock",
        "domain",
        "done",
        "done_all",
        "donut_large",
        "donut_small",
        "drafts",
        "drag_handle",
        "drive_eta",
        "dvr",
        "edit",
        "edit_location",
        "eject",
        "email",
        "enhanced_encryption",
        "equalizer",
        "error",
        "error_outline",
        "euro_symbol",
        "ev_station",
        "event",
        "event_available",
        "event_busy",
        "event_note",
        "event_seat",
        "exit_to_app",
        "expand_less",
        "expand_more",
        "explicit",
        "explore",
        "exposure",
        "exposure_neg_1",
        "exposure_neg_2",
        "exposure_plus_1",
        "exposure_plus_2",
        "exposure_zero",
        "extension",
        "face",
        "fast_forward",
        "fast_rewind",
        "favorite",
        "favorite_border",
        "featured_play_list",
        "featured_video",
        "feedback",
        "fiber_dvr",
        "fiber_manual_record",
        "fiber_new",
        "fiber_pin",
        "fiber_smart_record",
        "file_download",
        "file_upload",
        "filter",
        "filter_1",
        "filter_2",
        "filter_3",
        "filter_4",
        "filter_5",
        "filter_6",
        "filter_7",
        "filter_8",
        "filter_9",
        "filter_9_plus",
        "filter_b_and_w",
        "filter_center_focus",
        "filter_drama",
        "filter_frames",
        "filter_hdr",
        "filter_list",
        "filter_none",
        "filter_tilt_shift",
        "filter_vintage",
        "find_in_page",
        "find_replace",
        "fingerprint",
        "first_page",
        "fitness_center",
        "flag",
        "flare",
        "flash_auto",
        "flash_off",
        "flash_on",
        "flight",
        "flight_land",
        "flight_takeoff",
        "flip",
        "flip_to_back",
        "flip_to_front",
        "folder",
        "folder_open",
        "folder_shared",
        "folder_special",
        "font_download",
        "format_align_center",
        "format_align_justify",
        "format_align_left",
        "format_align_right",
        "format_bold",
        "format_clear",
        "format_color_fill",
        "format_color_reset",
        "format_color_text",
        "format_indent_decrease",
        "format_indent_increase",
        "format_italic",
        "format_line_spacing",
        "format_list_bulleted",
        "format_list_numbered",
        "format_paint",
        "format_quote",
        "format_shapes",
        "format_size",
        "format_strikethrough",
        "format_textdirection_l_to_r",
        "format_textdirection_r_to_l",
        "format_underlined",
        "forum",
        "forward",
        "forward_10",
        "forward_30",
        "forward_5",
        "free_breakfast",
        "fullscreen",
        "fullscreen_exit",
        "functions",
        "g_translate",
        "gamepad",
        "games",
        "gavel",
        "gesture",
        "get_app",
        "gif",
        "golf_course",
        "gps_fixed",
        "gps_not_fixed",
        "gps_off",
        "grade",
        "gradient",
        "grain",
        "graphic_eq",
        "grid_off",
        "grid_on",
        "group",
        "group_add",
        "group_work",
        "hd",
        "hdr_off",
        "hdr_on",
        "hdr_strong",
        "hdr_weak",
        "headset",
        "headset_mic",
        "healing",
        "hearing",
        "help",
        "help_outline",
        "high_quality",
        "highlight",
        "highlight_off",
        "history",
        "home",
        "hot_tub",
        "hotel",
        "hourglass_empty",
        "hourglass_full",
        "http",
        "https",
        "image",
        "image_aspect_ratio",
        "import_contacts",
        "import_export",
        "important_devices",
        "inbox",
        "indeterminate_check_box",
        "info",
        "info_outline",
        "input",
        "insert_chart",
        "insert_comment",
        "insert_drive_file",
        "insert_emoticon",
        "insert_invitation",
        "insert_link",
        "insert_photo",
        "invert_colors",
        "invert_colors_off",
        "iso",
        "keyboard",
        "keyboard_arrow_down",
        "keyboard_arrow_left",
        "keyboard_arrow_right",
        "keyboard_arrow_up",
        "keyboard_backspace",
        "keyboard_capslock",
        "keyboard_hide",
        "keyboard_return",
        "keyboard_tab",
        "keyboard_voice",
        "kitchen",
        "label",
        "label_outline",
        "landscape",
        "language",
        "laptop",
        "laptop_chromebook",
        "laptop_mac",
        "laptop_windows",
        "last_page",
        "launch",
        "layers",
        "layers_clear",
        "leak_add",
        "leak_remove",
        "lens",
        "library_add",
        "library_books",
        "library_music",
        "lightbulb_outline",
        "line_style",
        "line_weight",
        "linear_scale",
        "link",
        "linked_camera",
        "list",
        "live_help",
        "live_tv",
        "local_activity",
        "local_airport",
        "local_atm",
        "local_bar",
        "local_cafe",
        "local_car_wash",
        "local_convenience_store",
        "local_dining",
        "local_drink",
        "local_florist",
        "local_gas_station",
        "local_grocery_store",
        "local_hospital",
        "local_hotel",
        "local_laundry_service",
        "local_library",
        "local_mall",
        "local_movies",
        "local_offer",
        "local_parking",
        "local_pharmacy",
        "local_phone",
        "local_pizza",
        "local_play",
        "local_post_office",
        "local_printshop",
        "local_see",
        "local_shipping",
        "local_taxi",
        "location_city",
        "location_disabled",
        "location_off",
        "location_on",
        "location_searching",
        "lock",
        "lock_open",
        "lock_outline",
        "looks",
        "looks_3",
        "looks_4",
        "looks_5",
        "looks_6",
        "looks_one",
        "looks_two",
        "loop",
        "loupe",
        "low_priority",
        "loyalty",
        "mail",
        "mail_outline",
        "map",
        "markunread",
        "markunread_mailbox",
        "memory",
        "menu",
        "merge_type",
        "message",
        "mic",
        "mic_none",
        "mic_off",
        "mms",
        "mode_comment",
        "mode_edit",
        "monetization_on",
        "money_off",
        "monochrome_photos",
        "mood",
        "mood_bad",
        "more",
        "more_horiz",
        "more_vert",
        "motorcycle",
        "mouse",
        "move_to_inbox",
        "movie",
        "movie_creation",
        "movie_filter",
        "multiline_chart",
        "music_note",
        "music_video",
        "my_location",
        "nature",
        "nature_people",
        "navigate_before",
        "navigate_next",
        "navigation",
        "near_me",
        "network_cell",
        "network_check",
        "network_locked",
        "network_wifi",
        "new_releases",
        "next_week",
        "nfc",
        "no_encryption",
        "no_sim",
        "not_interested",
        "note",
        "note_add",
        "notifications",
        "notifications_active",
        "notifications_none",
        "notifications_off",
        "notifications_paused",
        "offline_pin",
        "ondemand_video",
        "opacity",
        "open_in_browser",
        "open_in_new",
        "open_with",
        "pages",
        "pageview",
        "palette",
        "pan_tool",
        "panorama",
        "panorama_fish_eye",
        "panorama_horizontal",
        "panorama_vertical",
        "panorama_wide_angle",
        "party_mode",
        "pause",
        "pause_circle_filled",
        "pause_circle_outline",
        "payment",
        "people",
        "people_outline",
        "perm_camera_mic",
        "perm_contact_calendar",
        "perm_data_setting",
        "perm_device_information",
        "perm_identity",
        "perm_media",
        "perm_phone_msg",
        "perm_scan_wifi",
        "person",
        "person_add",
        "person_outline",
        "person_pin",
        "person_pin_circle",
        "personal_video",
        "pets",
        "phone",
        "phone_android",
        "phone_bluetooth_speaker",
        "phone_forwarded",
        "phone_in_talk",
        "phone_iphone",
        "phone_locked",
        "phone_missed",
        "phone_paused",
        "phonelink",
        "phonelink_erase",
        "phonelink_lock",
        "phonelink_off",
        "phonelink_ring",
        "phonelink_setup",
        "photo",
        "photo_album",
        "photo_camera",
        "photo_filter",
        "photo_library",
        "photo_size_select_actual",
        "photo_size_select_large",
        "photo_size_select_small",
        "picture_as_pdf",
        "picture_in_picture",
        "picture_in_picture_alt",
        "pie_chart",
        "pie_chart_outlined",
        "pin_drop",
        "place",
        "play_arrow",
        "play_circle_filled",
        "play_circle_outline",
        "play_for_work",
        "playlist_add",
        "playlist_add_check",
        "playlist_play",
        "plus_one",
        "poll",
        "polymer",
        "pool",
        "portable_wifi_off",
        "portrait",
        "power",
        "power_input",
        "power_settings_new",
        "pregnant_woman",
        "present_to_all",
        "print",
        "priority_high",
        "public",
        "publish",
        "query_builder",
        "question_answer",
        "queue",
        "queue_music",
        "queue_play_next",
        "radio",
        "radio_button_checked",
        "radio_button_unchecked",
        "rate_review",
        "receipt",
        "recent_actors",
        "record_voice_over",
        "redeem",
        "redo",
        "refresh",
        "remove",
        "remove_circle",
        "remove_circle_outline",
        "remove_from_queue",
        "remove_red_eye",
        "remove_shopping_cart",
        "reorder",
        "repeat",
        "repeat_one",
        "replay",
        "replay_10",
        "replay_30",
        "replay_5",
        "reply",
        "reply_all",
        "report",
        "report_problem",
        "restaurant",
        "restaurant_menu",
        "restore",
        "restore_page",
        "ring_volume",
        "room",
        "room_service",
        "rotate_90_degrees_ccw",
        "rotate_left",
        "rotate_right",
        "rounded_corner",
        "router",
        "rowing",
        "rss_feed",
        "rv_hookup",
        "satellite",
        "save",
        "scanner",
        "schedule",
        "school",
        "screen_lock_landscape",
        "screen_lock_portrait",
        "screen_lock_rotation",
        "screen_rotation",
        "screen_share",
        "sd_card",
        "sd_storage",
        "search",
        "security",
        "select_all",
        "send",
        "sentiment_dissatisfied",
        "sentiment_neutral",
        "sentiment_satisfied",
        "sentiment_very_dissatisfied",
        "sentiment_very_satisfied",
        "settings",
        "settings_applications",
        "settings_backup_restore",
        "settings_bluetooth",
        "settings_brightness",
        "settings_cell",
        "settings_ethernet",
        "settings_input_antenna",
        "settings_input_component",
        "settings_input_composite",
        "settings_input_hdmi",
        "settings_input_svideo",
        "settings_overscan",
        "settings_phone",
        "settings_power",
        "settings_remote",
        "settings_system_daydream",
        "settings_voice",
        "share",
        "shop",
        "shop_two",
        "shopping_basket",
        "shopping_cart",
        "short_text",
        "show_chart",
        "shuffle",
        "signal_cellular_4_bar",
        "signal_cellular_connected_no_internet_4_bar",
        "signal_cellular_no_sim",
        "signal_cellular_null",
        "signal_cellular_off",
        "signal_wifi_4_bar",
        "signal_wifi_4_bar_lock",
        "signal_wifi_off",
        "sim_card",
        "sim_card_alert",
        "skip_next",
        "skip_previous",
        "slideshow",
        "slow_motion_video",
        "smartphone",
        "smoke_free",
        "smoking_rooms",
        "sms",
        "sms_failed",
        "snooze",
        "sort",
        "sort_by_alpha",
        "spa",
        "space_bar",
        "speaker",
        "speaker_group",
        "speaker_notes",
        "speaker_notes_off",
        "speaker_phone",
        "spellcheck",
        "star",
        "star_border",
        "star_half",
        "stars",
        "stay_current_landscape",
        "stay_current_portrait",
        "stay_primary_landscape",
        "stay_primary_portrait",
        "stop",
        "stop_screen_share",
        "storage",
        "store",
        "store_mall_directory",
        "straighten",
        "streetview",
        "strikethrough_s",
        "style",
        "subdirectory_arrow_left",
        "subdirectory_arrow_right",
        "subject",
        "subscriptions",
        "subtitles",
        "subway",
        "supervisor_account",
        "surround_sound",
        "swap_calls",
        "swap_horiz",
        "swap_vert",
        "swap_vertical_circle",
        "switch_camera",
        "switch_video",
        "sync",
        "sync_disabled",
        "sync_problem",
        "system_update",
        "system_update_alt",
        "tab",
        "tab_unselected",
        "tablet",
        "tablet_android",
        "tablet_mac",
        "tag_faces",
        "tap_and_play",
        "terrain",
        "text_fields",
        "text_format",
        "textsms",
        "texture",
        "theaters",
        "thumb_down",
        "thumb_up",
        "thumbs_up_down",
        "time_to_leave",
        "timelapse",
        "timeline",
        "timer",
        "timer_10",
        "timer_3",
        "timer_off",
        "title",
        "toc",
        "today",
        "toll",
        "tonality",
        "touch_app",
        "toys",
        "track_changes",
        "traffic",
        "train",
        "tram",
        "transfer_within_a_station",
        "transform",
        "translate",
        "trending_down",
        "trending_flat",
        "trending_up",
        "tune",
        "turned_in",
        "turned_in_not",
        "tv",
        "unarchive",
        "undo",
        "unfold_less",
        "unfold_more",
        "update",
        "usb",
        "verified_user",
        "vertical_align_bottom",
        "vertical_align_center",
        "vertical_align_top",
        "vibration",
        "video_call",
        "video_label",
        "video_library",
        "videocam",
        "videocam_off",
        "videogame_asset",
        "view_agenda",
        "view_array",
        "view_carousel",
        "view_column",
        "view_comfy",
        "view_compact",
        "view_day",
        "view_headline",
        "view_list",
        "view_module",
        "view_quilt",
        "view_stream",
        "view_week",
        "vignette",
        "visibility",
        "visibility_off",
        "voice_chat",
        "voicemail",
        "volume_down",
        "volume_mute",
        "volume_off",
        "volume_up",
        "vpn_key",
        "vpn_lock",
        "wallpaper",
        "warning",
        "watch",
        "watch_later",
        "wb_auto",
        "wb_cloudy",
        "wb_incandescent",
        "wb_iridescent",
        "wb_sunny",
        "wc",
        "web",
        "web_asset",
        "weekend",
        "whatshot",
        "widgets",
        "wifi",
        "wifi_lock",
        "wifi_tethering",
        "work",
        "wrap_text",
        "youtube_searched_for",
        "zoom_in",
        "zoom_out",
        "zoom_out_map"
      ]
      /*When global style change, that changes are broadcasted to all elements*/
      ed.global = {
        style: {
          header:{
            height: 64,
            enable: "initial",
            backgroundColor: "initial",
            title: {
              textColor: "initial",
              iconColor: "initial"
            }
          },
          border: {},
          backgroundColor: "initial"
        }
      };
    }

    ed.removePage = function (event, index) {
      event.stopPropagation();
      event.preventDefault();
      vm.dashboard.pages.splice(index, 1);
      $scope.$applyAsync();
    };

    ed.pagesEdit = function (ev) {
      $mdDialog.show({
        controller: PagesController,
        templateUrl: 'app/partials/edit/pagesDialog.html',
        parent: angular.element(document.body),
        targetEvent: ev,
        clickOutsideToClose:true,
        fullscreen: false, // Only for -xs, -sm breakpoints.
        openFrom: '.toolbarButtons',
        closeTo: '.toolbarButtons',
        locals: {
          dashboard: ed.dashboard,
          icons: ed.icons
        }
      })
      .then(function(page) {
        $scope.status = 'Dialog pages closed'
      }, function() {
        $scope.status = 'You cancelled the dialog.';
      });
    };

    ed.layersEdit = function (ev) {
      $mdDialog.show({
        controller: LayersController,
        templateUrl: 'app/partials/edit/layersDialog.html',
        parent: angular.element(document.body),
        targetEvent: ev,
        clickOutsideToClose:true,
        fullscreen: false, // Only for -xs, -sm breakpoints.
        openFrom: '.toolbarButtons',
        closeTo: '.toolbarButtons',
        locals: {
          dashboard: ed.dashboard,
          selectedpage: ed.selectedpage,
          selectedlayer: ed.selectedlayer
        }
      })
      .then(function(page) {
        $scope.status = 'Dialog layers closed'
      }, function() {
        $scope.status = 'You cancelled the dialog.';
      });
    };

    ed.datasourcesEdit = function (ev) {
      $mdDialog.show({
        controller: DatasourcesController,
        templateUrl: 'app/partials/edit/datasourcesDialog.html',
        parent: angular.element(document.body),
        targetEvent: ev,
        clickOutsideToClose:true,
        fullscreen: false, // Only for -xs, -sm breakpoints.
        openFrom: '.toolbarButtons',
        closeTo: '.toolbarButtons',
        locals: {
          dashboard: ed.dashboard,
          selectedpage: ed.selectedpage
        }
      })
      .then(function(page) {
        $scope.status = 'Dialog datasources closed'
      }, function() {
        $scope.status = 'You cancelled the dialog.';
      });
    };

    ed.dashboardEdit = function (ev) {
      $mdDialog.show({
        controller: EditDashboardController,
        templateUrl: 'app/partials/edit/editDashboardDialog.html',
        parent: angular.element(document.body),
        targetEvent: ev,
        clickOutsideToClose:true,
        fullscreen: false, // Only for -xs, -sm breakpoints.
        openFrom: '.toolbarButtons',
        closeTo: '.toolbarButtons',
        locals: {
          dashboard: ed.dashboard
        }
      })
      .then(function(page) {
        $scope.status = 'Dashboard Edit closed'
      }, function() {
        $scope.status = 'You cancelled the dialog.';
      });
    };

    ed.dashboardStyleEdit = function (ev) {
      $mdDialog.show({
        controller: EditDashboardStyleController,
        templateUrl: 'app/partials/edit/editDashboardStyleDialog.html',
        parent: angular.element(document.body),
        targetEvent: ev,
        clickOutsideToClose:true,
        fullscreen: false, // Only for -xs, -sm breakpoints.
        openFrom: '.toolbarButtons',
        closeTo: '.toolbarButtons',
        locals: {
          style: ed.global.style
        }
      })
      .then(function(page) {
        $scope.status = 'Dashboard Edit closed'
      }, function() {
        $scope.status = 'You cancelled the dialog.';
      });
    };

    ed.showDatalink = function (ev) {
      $mdDialog.show({
        controller: DatalinkController,
        templateUrl: 'app/partials/edit/datalinkDialog.html',
        parent: angular.element(document.body),
        targetEvent: ev,
        clickOutsideToClose:true,
        fullscreen: false, // Only for -xs, -sm breakpoints.
        openFrom: '.toolbarButtons',
        closeTo: '.toolbarButtons',
        locals: {
          dashboard: ed.dashboard,
          selectedpage: ed.selectedpage
        }
      })
      .then(function(page) {
        $scope.status = 'Dialog datalink closed'
      }, function() {
        $scope.status = 'You cancelled the dialog.';
      });
    };

    ed.savePage = function (ev) {
      ed.dashboard.interactionHash = interactionService.getInteractionHash();
      sofia2HttpService.saveDashboard(ed.id(), {"data":{"model":JSON.stringify(ed.dashboard),"id":"","identification":"a","customcss":"","customjs":"","jsoni18n":"","description":"a","public":ed.public}}).then(
        function(d){
          if(d){
            $mdDialog.show(
              $mdDialog.alert()
                .parent(angular.element(document.querySelector('document')))
                .clickOutsideToClose(true)
                .title('Dashboard Editor')
                .textContent('Your dashboard was successfully saved!')
                .ariaLabel('Save dialog')
                .ok('OK')
                .targetEvent(ev)
            )
          }
        }
      ).catch(
        function(d){
          if(d){
            console.log("Error: " + JSON.stringify(d))
            $mdDialog.show(
              $mdDialog.alert()
                .parent(angular.element(document.querySelector('document')))
                .clickOutsideToClose(true)
                .title('Dashboard Editor: ERROR')
                .textContent('There was an error saving your dashboard!')
                .ariaLabel('Save dialog')
                .ok('OK')
                .targetEvent(ev)
            )
          }
        }
      );
      //alert(JSON.stringify(ed.dashboard));
    };

    ed.changedOptions = function changedOptions() {
      //main.options.api.optionsChanged();
    };

    function PagesController($scope, $mdDialog, dashboard, icons, $timeout) {
      $scope.dashboard = dashboard;
      $scope.icons = icons;
      $scope.auxUpload = [];
      $scope.apiUpload = [];

      function auxReloadUploads(){
        /*Load previous images*/
        $timeout(function(){
          for(var page = 0; page < $scope.dashboard.pages.length; page ++){
            if($scope.dashboard.pages[page].background.file.length > 0){
              $scope.apiUpload[page].addRemoteFile($scope.dashboard.pages[page].background.file[0].filedata,$scope.dashboard.pages[page].background.file[0].lfFileName,$scope.dashboard.pages[page].background.file[0].lfTagType);
            }
          }
        });
      }

      auxReloadUploads();

      $scope.hide = function() {
        $mdDialog.hide();
      };

      $scope.cancel = function() {
        $mdDialog.cancel();
      };

      $scope.create = function() {
        var newPage = {};
        var newLayer = {};
        //newLayer.options = JSON.parse(JSON.stringify(ed.dashboard.pages[0].layers[0].options));
        newLayer.gridboard = [
          {
            cols: 8,
            rows: 7,
            y: 0,
            x: 0,
            id: "1",
            type: "livehtml",
            content: "<h1> LiveHTML Text </h1>",
            header: {
              enable: true,
              title: {
                icon: "",
                iconColor: "none",
                text: "Leaflet Map Gadget test",
                textColor: "none"
              },
              backgroundColor: "none",
              height: 64
            },
            backgroundColor: "initial",
            padding: 0,
            border: {
              color: "black",
              width: 1,
              radius: 5
            }
          }
        ];
        newLayer.title = "baseLayer";
        newPage.title = angular.copy($scope.title);
        newPage.icon = angular.copy($scope.selectedIconItem);
        newPage.layers = [newLayer];
        newPage.background = {}
        newPage.background.file = angular.copy($scope.file);
        newPage.background.color="hsl(0, 0%, 100%)";
        newPage.selectedlayer= 0;
        dashboard.pages.push(newPage);
        $scope.title = "";
        $scope.icon = "";
        $scope.file = [];
        auxReloadUploads();
        $scope.$applyAsync();
      };

      $scope.onFilesChange = function(index){
        dashboard.pages[index].background.file = $scope.auxUpload[index].file;
        if(dashboard.pages[index].background.file.length > 0){
          var FR = new FileReader();
          FR.onload = function(e) {
            dashboard.pages[index].background.filedata = e.target.result;
          };
          FR.readAsDataURL( dashboard.pages[index].background.file[0].lfFile );
        }
        else{
          dashboard.pages[index].background.filedata="";
        }
      }

      $scope.delete = function(index){
        dashboard.pages.splice(index, 1);
      }

      $scope.queryIcon = function (query) {
        return query ? $scope.icons.filter( createFilterFor(query) ) : $scope.icons;
      }

      /**
       * Create filter function for a query string
       */
      function createFilterFor(query) {
        var lowercaseQuery = angular.lowercase(query);
        return function filterFn(icon) {
          return (icon.indexOf(lowercaseQuery) != -1);
        };
      }

      $scope.moveUpPage = function(index){
        var temp = dashboard.pages[index];
        dashboard.pages[index] = dashboard.pages[index-1];
        dashboard.pages[index-1] = temp;
      }

      $scope.moveDownPage = function(index){
        var temp = dashboard.pages[index];
        dashboard.pages[index] = dashboard.pages[index+1];
        dashboard.pages[index+1] = temp;
      }
    }

    function LayersController($scope, $mdDialog, dashboard, selectedpage, selectedlayer) {
      $scope.dashboard = dashboard;
      $scope.selectedpage = selectedpage;
      $scope.selectedlayer = selectedlayer;
      $scope.hide = function() {
        $mdDialog.hide();
      };

      $scope.cancel = function() {
        $mdDialog.cancel();
      };

      $scope.create = function() {
        var newLayer = {}
        newLayer.gridboard = [
          {cols: 5, rows: 5, y: 0, x: 0, type:"mixed"},
          {cols: 2, rows: 2, y: 0, x: 2, hasContent: true},
          {cols: 1, rows: 1, y: 0, x: 4, type:"polar"},
          {cols: 1, rows: 1, y: 2, x: 5, type:"map"},
          {cols: 2, rows: 2, y: 1, x: 0}
        ];
        newLayer.title = angular.copy($scope.title);
        dashboard.pages[$scope.selectedpage].layers.push(newLayer);
        $scope.selectedlayer = dashboard.pages[$scope.selectedpage].layers.length-1;
        $scope.title = "";
        $scope.$applyAsync();
      };

      $scope.delete = function(index){
        dashboard.pages[$scope.selectedpage].layers.splice(index, 1);
      }

      $scope.queryIcon = function (query) {
        return query ? $scope.icons.filter( createFilterFor(query) ) : $scope.icons;
      }

      /**
       * Create filter function for a query string
       */
      function createFilterFor(query) {
        var lowercaseQuery = angular.lowercase(query);
        return function filterFn(icon) {
          return (icon.indexOf(lowercaseQuery) != -1);
        };
      }

      $scope.moveUpLayer = function(index){
        var temp = dashboard.pages[$scope.selectedpage].layers[index];
        dashboard.pages[$scope.selectedpage].layers[index] = dashboard.pages[$scope.selectedpage].layers[index-1];
        dashboard.pages[$scope.selectedpage].layers[index-1] = temp;
      }

      $scope.moveDownLayer = function(index){
        var temp = dashboard.pages[$scope.selectedpage].layers[index];
        dashboard.pages[$scope.selectedpage].layers[index] = dashboard.pages[$scope.selectedpage].layers[index+1];
        dashboard.pages[$scope.selectedpage].layers[index+1] = temp;
      }
    }

    function DatasourcesController($scope, $mdDialog, $http, dashboard, selectedpage) {
      $scope.dashboard = dashboard;
      $scope.selectedpage = selectedpage;
      $scope.datasources = [];
      $scope.hide = function() {
        $mdDialog.hide();
      };

      $scope.cancel = function() {
        $mdDialog.cancel();
      };

      $scope.create = function() {
        var datasource = angular.copy($scope.datasource);
        dashboard.pages[$scope.selectedpage].datasources[datasource.identification]={triggers:[],type:datasource.type,interval:datasources.refresh};
        $scope.name = "";
        $scope.$applyAsync();
      };

      $scope.delete = function(key){
        delete dashboard.pages[$scope.selectedpage].datasources[key];
      }

      $scope.loadDatasources = function(){
        return $http.get('/controlpanel/datasources/getUserGadgetDatasources').then(
          function(response){
            $scope.datasources=response.data;
          },
          function(e){
            console.log("Error getting datasources: " +  JSON.stringify(e))
          }
        );
      }
    }

    function EditDashboardController($scope, $mdDialog, dashboard, $timeout) {
      $scope.dashboard = dashboard;

      function auxReloadUploads(){
        /*Load previous images*/
        $timeout(function(){
          if($scope.dashboard.header.logo.file && $scope.dashboard.header.logo.file.length > 0){
            $scope.apiUpload.addRemoteFile($scope.dashboard.header.logo.filedata,$scope.dashboard.header.logo.file[0].lfFileName,$scope.dashboard.header.logo.file[0].lfTagType);
          }
        });
      }

      $scope.onFilesChange = function(){
        $scope.dashboard.header.logo.file = $scope.auxUpload.file;
        if($scope.dashboard.header.logo.file.length > 0){
          var FR = new FileReader();
          FR.onload = function(e) {
            $scope.dashboard.header.logo.filedata = e.target.result;
          };
          FR.readAsDataURL( $scope.dashboard.header.logo.file[0].lfFile );
        }
        else{
          $scope.dashboard.header.logo.filedata="";
        }
      }

      auxReloadUploads();

      $scope.hide = function() {
        $mdDialog.hide();
      };

      $scope.cancel = function() {
        $mdDialog.cancel();
      };

      $scope.changedOptions = function changedOptions() {
        $scope.dashboard.gridOptions.api.optionsChanged();
      };

    }

    function compareJSON(obj1, obj2) {
      var result = {};
      for(var key in obj1) {
          if(obj2[key] != obj1[key]) result[key] = obj2[key];
          if(typeof obj2[key] == 'array' && typeof obj1[key] == 'array')
              result[key] = compareJSON(obj1[key], obj2[key]);
          if(typeof obj2[key] == 'object' && typeof obj1[key] == 'object')
              result[key] = compareJSON(obj1[key], obj2[key]);
      }
      return result;
    }

    function EditDashboardStyleController($scope,$rootScope, $mdDialog, style, $timeout) {
      $scope.style = style;

      $scope.$watch('style',function(newValue, oldValue){
        if (newValue===oldValue) {
          return;
        }
        var diffs = compareJSON(oldValue, newValue);
        $rootScope.$broadcast('global.style', diffs);
      }, true)

      $scope.hide = function() {
        $mdDialog.hide();
      };

      $scope.cancel = function() {
        $mdDialog.cancel();
      };

    }

    function DatalinkController($scope,$rootScope, $mdDialog, interactionService, utilsService, sofia2HttpService, dashboard, selectedpage) {
      $scope.dashboard = dashboard;
      $scope.selectedpage = selectedpage;

      var rawInteractions = interactionService.getInteractionHash();

      initConnectionsList();
      generateGadgetsLists();

      function initConnectionsList(){
        $scope.connections = [];
        for(var source in rawInteractions){
          for(var indexFieldTargets in rawInteractions[source]){
            for(var indexTargets in rawInteractions[source][indexFieldTargets].targetList){
              var rowInteraction = {
                source:source,
                sourceField:rawInteractions[source][indexFieldTargets].emiterField,
                target:rawInteractions[source][indexFieldTargets].targetList[indexTargets].gadgetId,
                targetField:rawInteractions[source][indexFieldTargets].targetList[indexTargets].overwriteField
              }
              $scope.connections.push(rowInteraction);
            }
          }
        }
      }

      $scope.refreshGadgetEmitterFields = function(gadgetId){
        var gadget = findGadgetInDashboard(gadgetId);
        if(gadget == null){
          $scope.gadgetEmitterFields = [];
        }
        else{
          setGadgetEmitterFields(gadget);
        }
      }

      $scope.refreshGadgetTargetFields = function(gadgetId){
        var gadget = findGadgetInDashboard(gadgetId);
        if(gadget == null){
          $scope.gadgetEmitterFields = [];
        }
        else{
          setGadgetTargetFields(gadget);
        }
      }

      function setGadgetEmitterFields(gadget){
        switch(gadget.type){
          case "pie":
          case "bar":
            var gadgetMeasures = angular.element(document.getElementsByClassName(gadget.id)[0]).scope().$$childHead.vm.measures;
            $scope.emitterDatasource = gadgetMeasures[0].datasource.identification;
            $scope.gadgetEmitterFields = utilsService.sort_unique(gadgetMeasures.map(function(m){return m.config.fields[0]})).map(function(m){return {field:m}});
            break;
          case "radar":
            var gadgetMeasures = angular.element(document.getElementsByClassName(gadget.id)[0]).scope().$$childHead.vm.measures;
            $scope.emitterDatasource = gadgetMeasures[0].datasource.identification;
            $scope.gadgetEmitterFields = utilsService.sort_unique(gadgetMeasures.map(function(m){return m.config.fields[0]})).map(function(m){return {field:m}});
            break;
          case "map":
            var gadgetMeasures = angular.element(document.getElementsByClassName(gadget.id)[0]).scope().$$childHead.vm.measures;
            $scope.emitterDatasource = gadgetMeasures[0].datasource.identification;
            $scope.gadgetEmitterFields = utilsService.sort_unique(gadgetMeasures.map(function(m){return m.config.fields[2]})).map(function(m){return {field:m}});
            break;
          case "livehtml":
            var gadgetData = angular.element(document.getElementsByClassName(gadget.id)[0]).scope().$$childHead.vm;
            $scope.emitterDatasource = gadgetData.datasource.name;
            sofia2HttpService.getFieldsFromDatasourceId(gadgetData.datasource.id).then(
              function(data){
                $scope.gadgetEmitterFields = utilsService.getJsonFields(data.data[0],"", []);
              }
            )
            $scope.gadgetEmitterFields = [];
            break;
            case "table":
            var gadgetMeasures = angular.element(document.getElementsByClassName(gadget.id)[0]).scope().$$childHead.vm.measures;
            $scope.emitterDatasource = gadgetMeasures[0].datasource.identification;
            $scope.gadgetEmitterFields = utilsService.sort_unique(gadgetMeasures.map(function(m){return m.config.fields[0]})).map(function(m){return {field:m}});
            break;
        }
      }

      //Destination are all gadget fields
      function setGadgetTargetFields(gadget){
        $scope.targetDatasource="";
        var gadgetData = angular.element(document.getElementsByClassName(gadget.id)[0]).scope().$$childHead.vm;
        if(gadget.type === 'livehtml'){
          $scope.targetDatasource = gadgetData.datasource.name;
          var dsId = gadgetData.datasource.id;
        }
        else{
          $scope.targetDatasource = gadgetData.measures[0].datasource.identification;
          var dsId = gadgetData.measures[0].datasource.id;
        }
        sofia2HttpService.getFieldsFromDatasourceId(dsId).then(
          function(data){
            $scope.gadgetTargetFields = utilsService.getJsonFields(data.data[0],"", []);
          }
        )
        $scope.gadgetTargetFields = [];
      }

      //Get gadget JSON and return string info for UI
      $scope.prettyGadgetInfo = function(gadget){
        return gadget.header.title.text + " (" + gadget.type + ")"
      }

      $scope.generateGadgetInfo = function (gadgetId){
        var gadget = findGadgetInDashboard(gadgetId);
        if(gadget == null){
          return gadgetId;
        }
        else{
          return $scope.prettyGadgetInfo(gadget)
        }
      }

      function generateGadgetsLists(){
        $scope.gadgetsSources = getGadgetsSourcesInDashboard();
        $scope.gadgetsTargets = getGadgetsInDashboard();
      }

      //Generate gadget list of posible Sources of interactions: pie, bar, livehtml
      function getGadgetsSourcesInDashboard(){
        var gadgets = [];
        var page = $scope.dashboard.pages[$scope.selectedpage];       
          for (var i = 0; i < page.layers.length; i++) {
          var layer = page.layers[i];
          var gadgetsAux = layer.gridboard.filter(function(gadget){return typeGadgetList.indexOf(gadget.type) != -1});
          if(gadgetsAux.length){
            gadgets = gadgets.concat(gadgetsAux);
          }
        }
        return gadgets;
      }

      //Generate gadget list of posible Sources of interactions: pie, bar, livehtml
      function getGadgetsInDashboard(){
        var gadgets = [];
        var page = $scope.dashboard.pages[$scope.selectedpage];      
          for (var i = 0; i < page.layers.length; i++) {
          var layer = page.layers[i];
          var gadgetsAux = layer.gridboard;
          if(gadgetsAux.length){
            gadgets = gadgets.concat(gadgetsAux);
          }
        }
        return gadgets;
      }

      function findGadgetInDashboard(gadgetId){
        var page = $scope.dashboard.pages[$scope.selectedpage];      
          for (var i = 0; i < page.layers.length; i++) {
          var layer = page.layers[i];
          var gadgets = layer.gridboard.filter(function(gadget){return gadget.id === gadgetId});
          if(gadgets.length){
            return gadgets[0];
          }
        }
        return null;
      }

      $scope.create = function(sourceGadgetId, originField , targetGadgetId, destinationField) {
        if(sourceGadgetId && originField && targetGadgetId && destinationField){
          interactionService.registerGadgetInteractionDestination(sourceGadgetId, targetGadgetId, originField, destinationField);
          initConnectionsList();
        }
      };

      $scope.delete = function(sourceGadgetId, targetGadgetId, originField, destinationField){
        interactionService.unregisterGadgetInteractionDestination(sourceGadgetId, originField, targetGadgetId, destinationField);
        initConnectionsList();
      }

      $scope.hide = function() {
        $mdDialog.hide();
      };

      $scope.cancel = function() {
        $mdDialog.cancel();
      };

    }

    ed.showListBottomSheet = function() {
      $mdBottomSheet.show({
        templateUrl: 'app/partials/edit/addWidgetBottomSheet.html',
        controller: AddWidgetBottomSheetController,
        disableParentScroll: false,
        disableBackdrop: true,
        clickOutsideToClose: true
      }).then(function(clickedItem) {
        $scope.alert = clickedItem['name'] + ' clicked!';
      }).catch(function(error) {
        // User clicked outside or hit escape
      });
    };

    function AddWidgetBottomSheetController($scope, $mdBottomSheet){

    }

    $scope.$on('deleteElement',function (event, item) {
      var dashboard = $scope.ed.dashboard;
      var page = dashboard.pages[$scope.ed.selectedpage];
      var layer = page.layers[page.selectedlayer];
      layer.gridboard.splice(layer.gridboard.indexOf(item), 1);
      $scope.$applyAsync();
    });
  }
})();
