package com.banana.projectapp.profile;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import com.banana.projectapp.DataHolder;
import com.banana.projectapp.communication.ClientStub;
import com.banana.projectapp.db.DBManager;
import com.banana.projectapp.R;
import com.banana.projectapp.exception.EmberTokenInvalid;
import com.banana.projectapp.exception.SocialAccountInvalid;
import com.banana.projectapp.exception.UserInvalid;
import com.banana.projectapp.main.LoginActivity;
import com.banana.projectapp.main.MainFragmentActivity;
import com.banana.projectapp.social.ChooseSocial;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment{

//    static List<Bitmap> result = new ArrayList<>();
    SocialAdapter adapter;
	ListView list;
    private ClientStub client;
    TextView nome;
    TextView email;

//    DownloadSocialsImagesTask socialsImagesTask;
    ChangeMailTask changeMailTask;
    ChangePasswordTask changePasswordTask;
    DeleteSocialTask deleteSocialTask;

    DeleteAccountTask deleteAccountTask;

    private List<Social> socials;

    public void updateView(){

        DBManager db = new DBManager(getActivity()).open();
        socials = db.getSocials();
        db.close();

        adapter = new com.banana.projectapp.profile.SocialAdapter(getActivity(), socials);
        list.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        Log.e("ciao","notify "+ socials.size());
    }

    public void onActivityResult(int a, int b, Intent i){
        Log.e("ciao","on activity result");
        updateView();
    }

	public static ProfileFragment newInstance() {
		return new ProfileFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        try {
            client = new ClientStub(getActivity());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroy(){
//        if (socialsImagesTask!= null)
//            socialsImagesTask.cancel(true);
        if (changeMailTask!= null)
            changeMailTask.cancel(true);
        if (changePasswordTask!= null)
            changePasswordTask.cancel(true);
        if (deleteAccountTask!=null)
            deleteAccountTask.cancel(true);
        if (deleteSocialTask!= null)
            deleteSocialTask.cancel(true);

        super.onDestroy();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.profilo, container,
				false);
		nome = (TextView) rootView.findViewById(R.id.nome_utente);
		nome.setText(DataHolder.getEmail());
		nome.invalidate();
        email = (TextView) rootView.findViewById(R.id.email);
        email.setText(DataHolder.getEmail());
        email.invalidate();

        final Button change_mail = (Button) rootView.findViewById(R.id.change_email);
        change_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (changeMailTask != null){
                return;
            }

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            alert.setTitle("Change email");
            alert.setMessage("please enter your new email");

            final EditText input = new EditText(getActivity());
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String newMail = input.getText().toString();
                    changeMailTask = new ChangeMailTask(newMail);
                    changeMailTask.execute((Void) null);
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });

            alert.show();
            }
        });

        final Button change_password = (Button) rootView.findViewById(R.id.change_password);
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changePasswordTask != null){
                    return;
                }

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                alert.setTitle("Change Password");
                alert.setMessage("please enter your new password");

                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText password1 = new EditText(getActivity());
                password1.setHint("password");
                password1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                layout.addView(password1);

                final EditText password2 = new EditText(getActivity());
                password2.setHint("re-enter password");
                password2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                layout.addView(password2);

                alert.setView(layout);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String str1 = password1.getText().toString();
                        String str2 = password2.getText().toString();

                        if (str1.equals(str2)) {
                            changePasswordTask = new ChangePasswordTask(str1);
                            changePasswordTask.execute((Void) null);
                        } else {
                            Toast.makeText(getActivity(),"passwords must be equals",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.show();
            }
        });

        final Button addSocial = (Button) rootView.findViewById(R.id.addSocial);
        addSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ChooseSocial.class);
                startActivityForResult(intent, 1);


            }
        });

        final Button removeAccount = (Button) rootView.findViewById(R.id.removeAccount);
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if (deleteAccountTask != null){
                            return;
                        }
                        deleteAccountTask = new DeleteAccountTask(DataHolder.getEmail());
                        deleteAccountTask.execute((Void) null);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        removeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });

		list = (ListView) rootView.findViewById(R.id.list_view);

        DBManager db = new DBManager(getActivity()).open();
        socials = db.getSocials();
        db.close();

        adapter = new com.banana.projectapp.profile.SocialAdapter(getActivity(), socials);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Toast.makeText(getActivity(), socials.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        int fragment_id = 3;
        ((MainFragmentActivity) activity).onSectionAttached(fragment_id);
	}

    private class ChangeMailTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;

        ChangeMailTask(String email) {
            mEmail = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    String ember_token = DataHolder.getToken();
                    client.changeEmail(mEmail, ember_token);
                }
            } catch (UserInvalid | EmberTokenInvalid | IOException userInvalid) {
                userInvalid.printStackTrace();
            }

            return LoginActivity.isEmailValid(mEmail);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            changeMailTask = null;
            if (success) {
                DataHolder.setEmail(mEmail);
                nome.setText(DataHolder.getEmail());
                nome.invalidate();
                email.setText(DataHolder.getEmail());
                email.invalidate();
            } else {
                Toast.makeText(getActivity(),"sorry, email is not valid",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            changeMailTask = null;
            super.onCancelled();
        }
    }

    private class ChangePasswordTask extends AsyncTask<Void, Void, Boolean> {

        private final String mPassword;

        ChangePasswordTask(String password) {
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    String ember_token = DataHolder.getToken();
                    client.changePassword(mPassword, ember_token);
                }
                return true;
            } catch (IOException | EmberTokenInvalid e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            changePasswordTask = null;
        }

        @Override
        protected void onCancelled() {
            changePasswordTask = null;
            super.onCancelled();
        }
    }

    private class DeleteAccountTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;

        DeleteAccountTask(String email) {
            mEmail = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    String ember_token = DataHolder.getToken();
                    client.deleteYourAccount(mEmail, ember_token);
                }
                return true;
            } catch (IOException | EmberTokenInvalid | UserInvalid e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            deleteAccountTask = null;
            DataHolder.setToken(null);
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
        }

        @Override
        protected void onCancelled() {
            deleteAccountTask = null;
            super.onCancelled();
        }
    }

    private class DeleteSocialTask extends AsyncTask<Void, Void, Boolean> {

        private final int mSocialType;

        DeleteSocialTask(int socialType) {
            mSocialType = socialType;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                if (DataHolder.testing) {
                    String ember_token = DataHolder.getToken();
                    client.deleteSocial(mSocialType, ember_token);
                }
                return true;
            } catch (IOException | EmberTokenInvalid | SocialAccountInvalid e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            DBManager db = new DBManager(getActivity());
            db.open();
            db.remove(mSocialType, "SOCIALS");
            socials = db.getSocials();
            adapter.notifyDataSetChanged();
            db.close();
            deleteSocialTask = null;
        }

        @Override
        protected void onCancelled() {

            deleteSocialTask = null;
            super.onCancelled();
        }
    }
}
