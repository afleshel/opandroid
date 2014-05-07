using System;
using System.Diagnostics;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics.Contracts;
using Android.App;
using Android.Graphics;
using Android.Graphics.Drawables;
using Android.Views;
using Android.Widget;
using OpenPeerSdk.Helpers;
using HopSampleApp.Services;
using HopSampleApp.Views;
using System.Linq;
using Helpers = OpenPeerSdk.Helpers;
using BitmapType = Android.Graphics.Drawables.BitmapDrawable;

namespace HopSampleApp
{
	namespace Activities
	{
		[LoggerSubsystem("hop_sample_app")]
		public class SessionAdapter : BaseAdapter<object>
		{
			private String section;
			Activity context;
			IImageCachingDownloader downloader;
			SocialMediaFeature sm=new SocialMediaFeature();

			class AvatarDownloader
			{
				public Helpers.WeakReference<ViewHolder> binding;

				public AvatarDownloader (ViewHolder binding)
				{
					this.binding = binding;
				}

				public void HandleDownloaded (BitmapType bitmap)
				{
					ViewHolder holder = (ViewHolder)binding;
					if (this != holder.CurrentDownloader) {
						Logger.Trace ("SessionAdapter original view is no longer bound");
						return;
					}

					if (null == bitmap) {
						holder.AvatarImageView.SetImageDrawable (holder.OriginalEmptyAvatarDrawable);
						return;
					}

					UseBitmap (holder, bitmap);
				}

				private static void UseBitmap (ViewHolder holder, Bitmap bitmap)
				{
					holder.AvatarImageView.SetImageBitmap (bitmap);
				}

				private static void UseBitmap (ViewHolder holder, BitmapDrawable bitmap)
				{
					holder.AvatarImageView.SetImageDrawable (bitmap);
				}
			}

			class ViewHolder : Java.Lang.Object
			{
				public int AvatarWidth { get; set; }
				public int AvatarHeight { get; set; }
				public Drawable OriginalEmptyAvatarDrawable { get; set; }
				public ImageView AvatarImageView { get; set; }
				public ImageView AvatarHolder{ get; set;}
				public BadgeView BadgeView { get; set; }
				public TextView NameTextView { get; set; }
				public TextView UsernameTextView { get; set; }
				public TextView SessionTime{ get; set;}
				public LinearLayout Header{ get; set;}
				public TextView SessionName{ get; set;}


				public AvatarDownloader CurrentDownloader { get; set; }
			}

			public SessionAdapter (Activity context, IImageCachingDownloader downloader) : base() {
				this.context = context;
				this.downloader = downloader;
			}

			public override long GetItemId(int position)
			{
				return position;
			}

			public override object this[int position] {  
				get { return null; }
			}

			public override int Count {
				get { return 5; } //24
			}

			static string[] bogusUrls = {
				"http://biodegradablegeek.com/wp-content/uploads/2008/06/26.png",
				"http://vz.iminent.com/vz/9357179a-e957-4ab3-b043-d60448ed16fd/2/devil-smiley-avatars.gif",
				"http://biodegradablegeek.com/wp-content/uploads/2008/06/3.png",
				"http://www.avatar-zone.com/Avatars/Simpsons/Simpsons-40.gif",
				"http://www.avatarist.com/avatars/Movies/Monsters-Inc/Mike-(Monsters-Inc).jpg",
				"http://www.avatar-zone.com/Avatars/Fun/Avatar-9.jpg",
				"http://irrationalgames.com/files/avatars/40761/f9da7e5728b67cb6a791722d99c9e114-bpthumb.jpg",
				"http://fc07.deviantart.net/fs70/f/2014/051/d/4/wheeler_icon_by_th3frgt10warrior-d77c3p4.png",
				"http://i3.squidoocdn.com/resize_square/squidoo_images/50/lm7a13ab09b8972e27373a0b4f05c8deda_5fbb0ed49e0a6777569e70af09d31fb1.png",
				"https://www.neatoshop.com/images/product/93/6993/Doctor-Who-Dalek-USB-Desk-Protector_36726-s.jpg?v=36726",
				"http://www.jeboavatars.com/images/avatars/241738204067earthdawg.gif",
				"http://icons.iconarchive.com/icons/fixicon/farm/256/turtle-icon.png",
				"http://www.avatarist.com/avatars/Games/Super-Smash-Bros-Brawl/Mario-in-Brawl.gif",
				"http://avatarbox.net/avatars/img18/super_mario_kart_wario_avatar_picture_21060.gif",
				"http://icons.iconarchive.com/icons/3xhumed/mega-games-pack-33/128/Call-of-Duty-Modern-Warfare-2-8-icon.png",
				"http://www.jeboavatars.com/images/avatars/191415199067gagaga.jpg",
				"https://lh3.googleusercontent.com/-boHOsb5ui34/AAAAAAAAAAI/AAAAAAAAHjQ/mGPd8FsVMXA/w48-c-h48/photo.jpg",
				"http://25.media.tumblr.com/avatar_c80a99bd5fd4_128.png"
			};
			/* my session update */
			class SessionData
			{
				public int Id{ get; set;}
				public String SessionTypeName{ get; set;}
				public String Username{ get; set; }
				public DateTime SessionDate{ get; set;}
				public String SessionTime{ get; set;}
				public String SesisonUserName{ get;set;}
				public String SessionMyName{ get; set;}

			}
			List<SessionData> SessionType = new List<SessionData> ();
			public int GetSectionForPosition (int position)
			{

				return 1;
			}

			public int GetPositionForSection (int section)
			{
				//var character = sections [section];
				//var position = SessionType.FirstOrDefault (f => f.SessionTypeName == section);
				var position = SessionType.FirstOrDefault (f => f.Id == section);
				return position.Id;
			}

			public void BindSection(TextView s_value,View con,int position)
			{
				TextView txt = (TextView)con.FindViewById (Resource.Id.SessionName);
				LinearLayout header = (LinearLayout)con.FindViewById(Resource.Id.header);
				int curpos = GetSectionForPosition (position);
				if (GetPositionForSection(curpos)==position) {
					header.Visibility = ViewStates.Visible;
				} else {
					header.Visibility = ViewStates.Invisible;
				}

				   



			}
			/* end of my update */
			public override View GetView(int position, View convertView, ViewGroup parent)
			{
				View view = convertView; // re-use an existing view, if one is available

				ViewHolder holder;
				bool firstTimeResourceLoaded = false;

				if (view == null) { // otherwise create a new one
					firstTimeResourceLoaded = true;
					view = context.LayoutInflater.Inflate (HopSampleApp.Resource.Layout.ListItemSession, null);


					TextView badgeTextView = view.FindViewById<TextView> (Resource.Id.badgeAnchorTextView);

					BadgeView badgeView = new BadgeView (context, badgeTextView);
					badgeView.BadgePosition = BadgeView.Position.TopLeft;

					holder = new ViewHolder ();
					holder.AvatarImageView = view.FindViewById<ImageView> (Resource.Id.avatarImageView);
					holder.OriginalEmptyAvatarDrawable = holder.AvatarImageView.Drawable;
					holder.BadgeView = badgeView;
					holder.SessionTime = view.FindViewById<TextView> (Resource.Id.TimeStampDate);
					holder.NameTextView = view.FindViewById<TextView> (Resource.Id.nameTextView);
					holder.UsernameTextView = view.FindViewById<TextView> (Resource.Id.usernameTextView);
					holder.Header = view.FindViewById<LinearLayout> (Resource.Id.header);
					holder.SessionName = view.FindViewById<TextView> (Resource.Id.SessionName);
					holder.AvatarWidth = holder.AvatarImageView.LayoutParameters.Width;
					holder.AvatarHeight = holder.AvatarImageView.LayoutParameters.Height;


					// if these fail, you'll need to recode the source to delay the fetching of the avatar until after the render figures out the exact dimensions of the image
					Contract.Assume ( ((holder.AvatarWidth != ViewGroup.LayoutParams.MatchParent) && (holder.AvatarWidth != ViewGroup.LayoutParams.WrapContent)) );
					Contract.Assume ( ((holder.AvatarHeight != ViewGroup.LayoutParams.MatchParent) && (holder.AvatarHeight != ViewGroup.LayoutParams.WrapContent)) );

					view.Tag = holder;
				} else {
					holder = (ViewHolder)view.Tag;
				}

				object source = this [position];
				/* my simulated logic for session  */
				SessionType.Add (new SessionData
					{
						Id = 1,
						SessionDate = DateTime.Now,
						SessionTypeName = "Video Call",
						SessionTime = sm.Time_stamp(new DateTime(2014,4,20)),
						SesisonUserName = "petar-hookflash",
						SessionMyName = "Petar"
										
				});
				SessionType.Add (new SessionData
					{
						Id = 2,
						SessionDate = DateTime.Now,
						SessionTypeName = "Chat",
						SessionTime = sm.Time_stamp(new DateTime(2014,5,07)),
						SesisonUserName = "sergej-hookflash",
						SessionMyName = "Sergej"

					});
				SessionType.Add (new SessionData
					{
						Id = 3,
						SessionDate = DateTime.Now,
						SessionTypeName = "Video Call",
						SessionTime = sm.Time_stamp(new DateTime(2014,5,06)),
						SesisonUserName = "robin-hookflash",
						SessionMyName = "Robin"

					});
				SessionType.Add (new SessionData
					{
						Id=4,
						SessionDate = DateTime.Now,
						SessionTypeName = "Chat",
						SessionTime = sm.Time_stamp(new DateTime(2014,5,06)),
						SesisonUserName = "marko-hookflash",
						SessionMyName = "Marko"

					});
				//string data="Video Call";

				var type_data = SessionType.OrderBy (st => st.SessionTypeName == st.SessionTypeName + position.ToString()).ToList();//( from data in SessionType where data.Id == position select data).ToList();//SessionType.Where(data=>data.Id==position).OrderBy (st => st.SessionTypeName).ToList();
				foreach (var item in type_data) {
					    
					//BindSection (holder.SessionName,view, position);
					holder.SessionName.Text = item.SessionTypeName + position.ToString();
					holder.UsernameTextView.Text = item.SesisonUserName + position.ToString ();
					holder.SessionTime.Text = item.SessionTime;
					holder.BadgeView.Text = position.ToString ();
					holder.BadgeView.Show ();


				
					

					//} else {
					//	holder.Header.Visibility = ViewStates.Gone;
					//	}
						


					//holder.NameTextView.Text = item.SessionMyName + position.ToString ();

				}


			

				/*  end of simulation logic */

				//holder.NameTextView.Text = "My Name " + position.ToString();
				//holder.UsernameTextView.Text = "Username" + position.ToString();
				//holder.SessionTime.Text = sm.Time_stamp (new DateTime(2014,4,20));
				//holder.BadgeView.Text = position.ToString();
				//holder.BadgeView.Show ();
				/*holder.SessionName.Text = "Video Call" + position.ToString ();
				holder.NameTextView.Text = "My Name " + position.ToString();
				holder.UsernameTextView.Text = "Username" + position.ToString();
				holder.SessionTime.Text = sm.Time_stamp (new DateTime(2014,4,20));
				holder.BadgeView.Text = position.ToString();
				holder.BadgeView.Show ();*/

				holder.CurrentDownloader = new AvatarDownloader (holder);

				BitmapType bitmap = downloader.FetchNowOrAsyncDownload (
					bogusUrls [position % bogusUrls.Length],
					holder.AvatarWidth,
					holder.AvatarHeight,
					holder.CurrentDownloader.HandleDownloaded
					);

				if (null != bitmap)
				{
					holder.AvatarImageView.SetImageDrawable (bitmap);
				}
				else 
				{
					if (!firstTimeResourceLoaded) {
						holder.AvatarImageView.SetImageDrawable (holder.OriginalEmptyAvatarDrawable);	// reset back to original drawable
					}
				}


				return view;
			}


		}
	}
}

