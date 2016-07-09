var express = require('express'),
  router = express.Router(),
  Article = require('../models/article'),
  request = require('request');

module.exports = function (app) {
  app.use('/', router);
};

var promisedRequest = function (url) {
    return new Promise(function (resolve, reject) {
        request({
            url: url,
            headers: {
                'User-Agent': 'curl/7.43.0'
            },
            json: true
        }, function (err, resp, body) {
            if (err) {
                console.log(err);
                reject(err);
            } else {
                if (resp.statusCode !== 200) {
                    reject({ foo: 'bar' });
                } else {
                    resolve(body);
                }
            }
        });
    });
};

var fiddleWithResponse = function (github) {
    var newGithub = github;
    newGithub.current_user_url = 'weeeeee';

    return newGithub;
};

router.get('/', function (req, res, next) {
    var articles = [new Article(), new Article()];
    promisedRequest('https://api.githu.com')
        .then(fiddleWithResponse)
        .then(function (github) {
            res.render('index', {
                title: 'Generator-Express MVC',
                articles: articles,
                github: github
            });
        })
        .catch (function () {
            res.status = 500;
            res.render('error', {
                message: 'foobar',
                error: {
                    status: 'githu is doewn',
                    stack: 'no gonnae give a stacktrace'
                }
            });
        });
});
