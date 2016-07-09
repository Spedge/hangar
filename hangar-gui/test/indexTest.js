var expect = require('chai').expect,
    request = require('supertest'),
    express = require('express'),
    nock = require('nock');

var app = require('../app');


describe('the hangar gui', function () {
    it('should return a front page', function (done) {
        nock('https://api.githu.com')
            .get('/')
            .reply(200, { current_user_url: 'in a test' });

        request(app)
            .get('/')
            .expect(200, done);
    });
});
