package com.example.youtubehome.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.youtubehome.GroupChatActivity;
import com.example.youtubehome.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GroupsFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listOfGroups = new ArrayList<>();

    private DatabaseReference GroupRef;
    private Iterator iterator;

    public static String KEY_GROUP_CHAT = "group_chat";

    public static GroupsFragment newInstance() {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_groups_fragment, container, false);
        initListAndBase(view);
        RetrievAndShowGroups();
        return view;
    }

    private void initListAndBase(View view) {
        listView = view.findViewById(R.id.listView);
        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listOfGroups);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentGroupChatName = parent.getItemAtPosition(position).toString();
                Intent group_chat_intent = new Intent(getContext(), GroupChatActivity.class);
                group_chat_intent.putExtra(KEY_GROUP_CHAT, currentGroupChatName);
                startActivity(group_chat_intent);
            }
        });
    }

    private void RetrievAndShowGroups() {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Set<String> set = new HashSet<>();
                iterator = snapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    set.add(((DataSnapshot) iterator.next()).getKey());
                }
                listOfGroups.clear();
                listOfGroups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}