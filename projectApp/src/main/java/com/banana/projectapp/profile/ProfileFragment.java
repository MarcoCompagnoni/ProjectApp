package com.banana.projectapp.profile;

import java.io.IOException;
import java.util.List;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.R;
import com.banana.projectapp.exception.AuthTokenInvalid;
import com.banana.projectapp.exception.NoConnectionException;
import com.banana.projectapp.main.LoginFBActivity;
import com.banana.projectapp.main.MainFragmentActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment{

	ListView list;
    private ClientStub client;
    TextView nome;
    TextView email;
    TextView credits;
    ImageView imageView;

//    DownloadSocialsImagesTask socialsImagesTask;
//    ChangeMailTask changeMailTask;
//    ChangePasswordTask changePasswordTask;
//    DeleteSocialTask deleteSocialTask;

    DeleteAccountTask deleteAccountTask;

    private List<Social> socials;

//    public void updateView(){
//
//        DBManager db = new DBManager(getActivity()).open();
//        socials = db.getSocials();
//        db.close();
//
//        adapter = new com.banana.projectapp.profile.SocialAdapter(getActivity(), socials);
//        list.setAdapter(adapter);
//
//        adapter.notifyDataSetChanged();
//        Log.e("ciao","notify "+ socials.size());
//    }

    public void onActivityResult(int a, int b, Intent i){
        Log.e("ciao","on activity result");
        //updateView();
        super.onActivityResult(a,b,i);
    }

	public static ProfileFragment newInstance() {
		return new ProfileFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        client = new ClientStub();
    }

    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroy(){
//        if (changeMailTask!= null)
//            changeMailTask.cancel(true);
//        if (changePasswordTask!= null)
//            changePasswordTask.cancel(true);
        if (deleteAccountTask!=null)
            deleteAccountTask.cancel(true);
//        if (deleteSocialTask!= null)
//            deleteSocialTask.cancel(true);

        super.onDestroy();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.profilo, container,
				false);
		nome = (TextView) rootView.findViewById(R.id.nome_utente);
        nome.setText(DataHolder.getUserName());
        nome.invalidate();
//        email = (TextView) rootView.findViewById(R.id.email);
//        email.setText(DataHolder.getUserName());
//        email.invalidate();
        credits = (TextView) rootView.findViewById(R.id.numero_crediti);
        credits.setText(DataHolder.getCredits()+" CR");
        credits.invalidate();
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        imageView.setImageBitmap(DataHolder.getMyProfile().getPhoto());
        imageView.invalidate();

//        final Button change_mail = (Button) rootView.findViewById(R.id.change_email);
//        change_mail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            if (changeMailTask != null){
//                return;
//            }
//
//            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//
//            alert.setTitle("Change email");
//            alert.setMessage("please enter your new email");
//
//            final EditText input = new EditText(getActivity());
//            alert.setView(input);
//
//            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    String newMail = input.getText().toString();
//                    changeMailTask = new ChangeMailTask(newMail);
//                    changeMailTask.execute((Void) null);
//                }
//            });
//
//            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                }
//            });
//
//            alert.show();
//            }
//        });
//
//        final Button change_password = (Button) rootView.findViewById(R.id.change_password);
//        change_password.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (changePasswordTask != null){
//                    return;
//                }
//
//                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//
//                alert.setTitle("Change Password");
//                alert.setMessage("please enter your new password");
//
//                LinearLayout layout = new LinearLayout(getActivity());
//                layout.setOrientation(LinearLayout.VERTICAL);
//
//                final EditText password1 = new EditText(getActivity());
//                password1.setHint("password");
//                password1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                password1.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                layout.addView(password1);
//
//                final EditText password2 = new EditText(getActivity());
//                password2.setHint("re-enter password");
//                password2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                password2.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                layout.addView(password2);
//
//                alert.setView(layout);
//
//                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        String str1 = password1.getText().toString();
//                        String str2 = password2.getText().toString();
//
//                        if (str1.equals(str2)) {
//                            changePasswordTask = new ChangePasswordTask(str1);
//                            changePasswordTask.execute((Void) null);
//                        } else {
//                            Toast.makeText(getActivity(),"passwords must be equals",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                    }
//                });
//
//                alert.show();
//            }
//        });
//
//        final Button addSocial = (Button) rootView.findViewById(R.id.addSocial);
//        addSocial.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(getActivity(), LoginFBActivity.class);
//                startActivityForResult(intent, 1);
//
//
//            }
//        });

//        final Button removeAccount = (Button) rootView.findViewById(R.id.removeAccount);
//        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which){
//                    case DialogInterface.BUTTON_POSITIVE:
//                        if (deleteAccountTask != null){
//                            return;
//                        }
//                        deleteAccountTask = new DeleteAccountTask();
//                        deleteAccountTask.execute((Void) null);
//                        break;
//                    case DialogInterface.BUTTON_NEGATIVE:
//                        break;
//                }
//            }
//        };
//        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        removeAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                builder.setMessage(getString(R.string.are_you_sure))
//                        .setPositiveButton(getString(R.string.yes), dialogClickListener)
//                        .setNegativeButton(getString(R.string.no), dialogClickListener).show();
//
//            }
//        });

//		list = (ListView) rootView.findViewById(R.id.list_view);
//
//        DBManager db = new DBManager(getActivity()).open();
//        socials = db.getSocials();
//        db.close();
//
//        adapter = new com.banana.projectapp.profile.SocialAdapter(getActivity(), socials);
//        list.setAdapter(adapter);
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                    int position, long id) {
//                Toast.makeText(getActivity(), socials.get(position).getName(), Toast.LENGTH_SHORT).show();
//            }
//        });

        return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        int fragment_id = 3;
        ((MainFragmentActivity) activity).onSectionAttached(fragment_id);
	}

//    private class ChangeMailTask extends AsyncTask<Void, Void, Boolean> {
//
//        private final String mEmail;
//
//        ChangeMailTask(String email) {
//            mEmail = email;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//
//            if (!LoginActivity.isEmailValid(mEmail))
//                return false;
//
//            try {
//                if (DataHolder.testing) {
//                    Log.i("","chiamo client.change password con "+mEmail);
//                    String ember_token = DataHolder.getAuthToken();
//                    client.changeEmail(mEmail, ember_token);
//                }
//            } catch (UserInvalid | AuthTokenInvalid | IOException userInvalid) {
//                userInvalid.printStackTrace();
//                return false;
//            } catch (NoConnectionException e) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getActivity(),"No connection",Toast.LENGTH_SHORT).show();
//                    }
//                });
//                return false;
//            }
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//            changeMailTask = null;
//            if (success) {
//                DataHolder.setEmail(mEmail);
//                nome.setText(DataHolder.getUserName());
//                nome.invalidate();
//                email.setText(DataHolder.getUserName());
//                email.invalidate();
//            } else {
//                Toast.makeText(getActivity(),"sorry, email is not valid",Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            changeMailTask = null;
//            super.onCancelled();
//        }
//    }
//
//    private class ChangePasswordTask extends AsyncTask<Void, Void, Boolean> {
//
//        private final String mPassword;
//
//        ChangePasswordTask(String password) {
//            mPassword = password;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            if (!LoginActivity.isPasswordValid(mPassword))
//                return false;
//
//            try {
//                if (DataHolder.testing) {
//                    Log.i("","chiamo client.change password con "+mPassword);
//                    String ember_token = DataHolder.getAuthToken();
//                    client.changePassword(mPassword, ember_token);
//                }
//            } catch (IOException | AuthTokenInvalid e) {
//                e.printStackTrace();
//                return false;
//            } catch (NoConnectionException e) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getActivity(),"No connection",Toast.LENGTH_SHORT).show();
//                    }
//                });
//                return false;
//            }
//
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//            changePasswordTask = null;
//            if (!success){
//                Toast.makeText(getActivity(),"password is too short",Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            changePasswordTask = null;
//            super.onCancelled();
//        }
//    }

    private class DeleteAccountTask extends AsyncTask<Void, Void, Boolean> {

        DeleteAccountTask() {}

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    Log.i("","chiamo client.deleteaccount "+DataHolder.getAuthToken());
                    String ember_token = DataHolder.getAuthToken();
                    client.deleteYourAccount(ember_token);
                }
                return true;
            } catch (IOException | AuthTokenInvalid e) {
                e.printStackTrace();
                return false;
            } catch (NoConnectionException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                getString(R.string.no_connection)
                                ,Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            deleteAccountTask = null;
            if (success) {
                DataHolder.setAuthToken(null);
                DataHolder.getSession().closeAndClearTokenInformation();
                DataHolder.setSession(null);
                Intent intent = new Intent(getActivity(), LoginFBActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            deleteAccountTask = null;
            super.onCancelled();
        }
    }

//    private class DeleteSocialTask extends AsyncTask<Void, Void, Boolean> {
//
//        private final int mSocialType;
//
//        DeleteSocialTask(int socialType) {
//            mSocialType = socialType;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//
//            try {
//                if (DataHolder.testing) {
//                    String ember_token = DataHolder.getAuthToken();
//                    Log.i("","chiamo client.delete social");
//                    client.deleteSocial(mSocialType, ember_token);
//                }
//                return true;
//            } catch (IOException | AuthTokenInvalid | SocialTypeInvalid e) {
//                e.printStackTrace();
//                return false;
//            } catch (NoConnectionException e) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getActivity(),"No connection",Toast.LENGTH_SHORT).show();
//                    }
//                });
//                return false;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//            if (success) {
//                DBManager db = new DBManager(getActivity());
//                db.open();
//                db.remove(mSocialType, "SOCIALS");
//                socials = db.getSocials();
//                adapter.notifyDataSetChanged();
//                db.close();
//            } else {
//                Toast.makeText(getActivity(),"c'è stato qualche imprevisto nel cancellare il social biricchino",Toast.LENGTH_SHORT).show();
//            }
//            deleteSocialTask = null;
//        }
//
//        @Override
//        protected void onCancelled() {
//
//            deleteSocialTask = null;
//            super.onCancelled();
//        }
//    }
}
