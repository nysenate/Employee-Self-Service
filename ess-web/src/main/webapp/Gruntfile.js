module.exports = function(grunt) {
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        /** Path locations to be used as templates */
        cssRoot: 'assets/css',
        cssSource: '<%= cssRoot %>/src',
        lessSource: '<%= cssRoot %>/less',
        cssVendor: '<%= cssRoot %>/vendor',
        cssDest: '<%= cssRoot %>/dest',
        jsRoot: 'assets/js',
        jsSource: '<%= jsRoot %>/src',
        jsVendor: '<%= jsRoot %>/vendor',
        jspSource: 'WEB-INF/view',
        tagSource: 'WEB-INF/tags',
        bowerRoot: 'bower_components',
        jsDest: '<%= jsRoot %>/dest',
        tomcatWeb: '/usr/share/tomcat/webapps/timesheets',

        /** Compile LESS into css and place it into the css source directory */
        less: {
            dev: {
                files: {
                    '<%= cssSource %>/main.css': ['<%= lessSource %>/**/*.less']
                }
            }
        },

        /** Minify all css into one file */
        cssmin: {
            combine: {
                src: ['<%= cssSource %>/*.css', '<%= cssVendor %>/*.css'],
                dest: '<%= cssDest %>/app.min.css'
            }
        },

        /** Compress all js into dev and prod files */
        uglify: {
            vendor: {
                options: {
                    mangle: false,
                    preserveComments: 'some'
                },
                files: {
                    '<%= jsDest %>/timesheets-vendor.min.js':
                        ['<%= bowerRoot %>/jquery/dist/jquery.min.js',
                         '<%= bowerRoot %>/jquery-ui/ui/minified/jquery.ui.core.min.js',
                         '<%= bowerRoot %>/jquery-ui/ui/minified/jquery.ui.widget.min.js',
                         '<%= bowerRoot %>/jquery-ui/ui/minified/jquery.ui.button.min.js',
                         '<%= bowerRoot %>/jquery-ui/ui/minified/jquery.ui.position.min.js',
                         '<%= bowerRoot %>/jquery-ui/ui/minified/jquery.ui.dialog.min.js',
                         '<%= bowerRoot %>/jquery-ui/ui/minified/jquery.ui.datepicker.min.js',
                         '<%= bowerRoot %>/jquery-ui/ui/minified/jquery.ui.menu.min.js',
                         '<%= bowerRoot %>/jquery-ui/ui/minified/jquery.ui.autocomplete.min.js',
                         '<%= bowerRoot %>/angular/angular.min.js',
                         '<%= bowerRoot %>/angular-route/angular-route.min.js',
                         '<%= bowerRoot %>/angular-resource/angular-resource.min.js',
                         '<%= bowerRoot %>/angular-animate/angular-animate.min.js',
                         '<%= bowerRoot %>/angular-utils-pagination/dirPagination.js',
                         '<%= bowerRoot %>/odometer/odometer.min.js',
                         '<%= bowerRoot %>/moment/min/moment.min.js',
                         '<%= bowerRoot %>/floatThead/dist/jquery.floatThead.min.js',
                         '<%= bowerRoot %>/angular-float-thead/angular-floatThead.js', 
                         '<%= bowerRoot %>/underscore/underscore-min.js',
                         '<%= bowerRoot %>/ui-autocomplete/autocomplete.js',
                         '<%= bowerRoot %>/nsPopover/src/nsPopover.js'
                         //'<%= bowerRoot %>/highcharts.com/highcharts.src.js'
                        ],
                    '<%= jsDest %>/timesheets-vendor-ie.min.js':
                        ['<%= bowerRoot %>/json2/json2.js']
                }
            },
            dev: {
            },
            prod: {
                options: {
                    compress: {
                        drop_console: true
                    },
                    preserveComments: 'some', /** Preserve licensing comments */
                    banner: '/*! <%= pkg.name %> - v<%= pkg.version %> - ' +'<%= grunt.template.today("yyyy-mm-dd") %> */'
                },
                files: {
                    '<%= jsDest %>/timesheets.min.js':
                        ['<%= jsSource %>/common/ess-app.js', '<%= jsSource %>/auth/*.js']
                }
            }
        },

        /** Automatically run certain tasks based on file changes */
        watch: {
            less: {
                files: ['<%= lessSource %>/**.less', '<%= lessSource %>/common/**.less'],
                tasks: ['less', 'cssmin', 'copy:css']
            },
            css: {
                files: ['<%= cssVendor %>/**/*.css', '<%= cssSource %>/**/*.css'],
                tasks: ['cssmin', 'copy:css']
            },
            jsVendor: {
                files: ['<%= bowerRoot %>/**.js'],
                tasks: ['uglify:vendor', 'copy:js']
            },
            jsSource: {
                files: ['<%= jsSource %>/**/*.js'],
                tasks: ['uglify:dev', 'uglify:prod', 'copy:js']
            },
            jsp: {
                files: ['<%= jspSource %>/**/*.jsp', '<%= tagSource %>/**/*.tag'],
                tasks: ['copy:jsp']
            }
        },

        copy: {
            css: {
                files: [{
                    expand:true, cwd: '<%= cssDest %>/', src: ['**'], filter: 'isFile',
                    dest: '<%= tomcatWeb %>/assets/css/dest/'
                }]
            },
            js: {
                files: [{
                    expand:true, src: ['<%= jsSource %>/**', '<%= jsDest %>/**'], filter: 'isFile',
                    dest: '<%= tomcatWeb %>'}]
            },
            jsp : {
                files: [{
                    expand:true, src: ['<%= jspSource %>/**', '<%= tagSource %>/**'], filter: 'isFile',
                    dest: '<%= tomcatWeb %>'
                }]
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-watch');

    grunt.registerTask('default', ['less', 'cssmin', 'uglify', 'copy']);
};