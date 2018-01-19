package com.indracompany.sofia2.libraries.social.twitter;

import java.util.List;

import org.springframework.social.twitter.api.DirectMessage;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;

import lombok.Getter;

public class TwitterServiceSpringSocialImpl {

	@Getter private Twitter twitter;
	
	protected TwitterServiceSpringSocialImpl(Twitter twitter) {
		this.twitter=twitter;
	}
	public TwitterProfile getProfile() {
		return this.twitter.userOperations().getUserProfile();		
	}
	public TwitterProfile getProfile(String name) {
		return this.twitter.userOperations().getUserProfile(name);		
	}
	public void updateStatus(String status) {
		twitter.timelineOperations().updateStatus(status);
	}
	public List<Tweet> getUserTimeLine() {
		return this.twitter.timelineOperations().getUserTimeline();		
	}
	public List<Tweet> getUserTimeLine(String user) {
		return this.twitter.timelineOperations().getUserTimeline(user);		
	}
	public void follow(String user) {
		twitter.friendOperations().follow(user);		
	}
	public void unfollow(String user) {
		twitter.friendOperations().unfollow(user);		
	}
	public List<TwitterProfile> getFollowers() {
		return this.twitter.friendOperations().getFollowers();	
	}
	public List<TwitterProfile> getFollowers(String user) {
		return this.twitter.friendOperations().getFollowers(user);	
	}
	public SearchResults search(String what) {
		return twitter.searchOperations().search(what);	
	}
	public SearchResults searchPage(String what,int page) {
		return twitter.searchOperations().search(what,page);	
	}
	public SearchResults search(String what,String lang) {
		SearchParameters params = new SearchParameters(what);
		params.lang(lang);
		return twitter.searchOperations().search(params);	
	}
	public SearchResults search(SearchParameters params) {
		return twitter.searchOperations().search(params);	
	}
	public void directMessage (String to, String what) {
		twitter.directMessageOperations().sendDirectMessage(to,what);
	}
	public List<DirectMessage> getDirectMessages() {
		return twitter.directMessageOperations().getDirectMessagesReceived();	
	}
	
}
